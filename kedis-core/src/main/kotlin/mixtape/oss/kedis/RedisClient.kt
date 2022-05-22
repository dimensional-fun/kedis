@file:OptIn(KedisInternalApi::class)

package mixtape.oss.kedis

import  io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mixtape.oss.kedis.annotations.KedisInternalApi
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.type.RedisTypeReader
import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.exception.RedisTypeUnknownException
import mixtape.oss.kedis.protocol.Protocol
import mixtape.oss.kedis.protocol.RedisType
import mixtape.oss.kedis.util.auth
import mixtape.oss.kedis.util.escaped
import mixtape.oss.kedis.util.quit
import mu.KotlinLogging
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal val log = KotlinLogging.logger { }

public open class RedisClient(
    public val uri: RedisURI,
    public val protocol: Protocol,
    private val scope: CoroutineScope,
    private val socket: Socket,
) {
    internal val mutex: Mutex = Mutex()

    @KedisInternalApi
    public val incoming: ByteReadChannel = socket.openReadChannel()

    @KedisInternalApi
    public val outgoing: ByteWriteChannel = socket.openWriteChannel()

    public val isClosed: Boolean
        get() = !scope.isActive || socket.isClosed

    public suspend fun sendPacket(bytes: ByteArray) {
        sendPacket(ByteReadPacket(bytes))
    }

    public suspend fun sendPacket(packet: ByteReadPacket) {
        outgoing.writePacket(packet)
        outgoing.flush()
    }

    public suspend fun <T> readReply(reader: RedisTypeReader<T>): T? {
        /* validate the type */
        val type = RedisType.find(incoming.readByte())
            ?: throw RedisTypeUnknownException()

        /* check if an error was returned. */
        when (type) {
            RedisType.SimpleError -> {
                val message = incoming.readUTF8Line()
                log.debug { "Received error: $message" }

                throw RedisProtocolException(message)
            }

            /* read from incoming. */
            else -> {
                log.debug { "Received type: $type" }

                /* read from incoming. */
                return reader.read(type, this)
            }
        }

    }

    public suspend fun close() {
        try {
            quit()
            socket.dispose()
            scope.cancel()
            log.debug { "Client has been closed." }
        } catch (ex: Exception) {
            log.error(ex) { "Unable to cleanly close redis connection:" }
        }
    }

    public suspend fun <T> executeCommand(command: RedisCommand<T>): T? {
        mutex.withLock {
            require (!socket.isClosed) {
                "The socket for this client has been closed."
            }

            val payload = command.write(protocol.writer).apply {
                log.info { "Sending ${command.name} -> ${decodeToString().escaped}" }
            }

            outgoing.writePacket(ByteReadPacket(payload))
            outgoing.flush()

            incoming.awaitContent()

            return readReply(command.reader)
        }
    }
}

public suspend fun RedisClient(uri: String, protocol: Protocol = Protocol.RESP2): RedisClient =
    RedisClient(RedisURI(uri), protocol)

public suspend fun RedisClient(uri: RedisURI, protocol: Protocol = Protocol.RESP2): RedisClient {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("Redis Client"))

    val socket = try {
        aSocket(ActorSelectorManager(scope.coroutineContext))
            .tcp()
            .connect(uri.address)
    } catch (e: Exception) {
        log.error(e) { "Unable to connect to $uri" }
        throw e
    }

    log.info { "Connected to $uri using protocol => $protocol" }

    val client = RedisClient(uri, protocol, scope, socket)
    if (protocol != Protocol.RESP2) {
        var hello = RedisCommand("HELLO", RedisTypeReader.Map, protocol.id.toString())
        if (uri.auth != null) {
            hello = hello.withOption("AUTH", uri.auth.username ?: "default", uri.auth.password)
        }

        val info = client.executeCommand(hello)
        log.info { "Received HELLO -> $info" }
    } else if (uri.auth != null) {
        client.auth(uri.auth)
    }

    return client
}

public inline fun RedisClient.pipelined(build: RedisPipeline.() -> Unit = {}): RedisPipeline {
    contract {
        callsInPlace(build, InvocationKind.EXACTLY_ONCE)
    }

    return RedisPipeline(this)
        .apply(build)
}
