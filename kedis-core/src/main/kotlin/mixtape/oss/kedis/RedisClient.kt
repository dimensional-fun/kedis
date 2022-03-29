@file:OptIn(KedisInternalApi::class)

package mixtape.oss.kedis

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mixtape.oss.kedis.annotations.KedisInternalApi
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.RedisTypeReader
import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.exception.RedisTypeUnknownException
import mixtape.oss.kedis.protocol.RedisType
import mixtape.oss.kedis.util.auth
import mixtape.oss.kedis.util.escaped
import mixtape.oss.kedis.util.quit
import mu.KotlinLogging
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal val log = KotlinLogging.logger { }

public open class RedisClient(public val uri: RedisURI, private val scope: CoroutineScope, private val socket: Socket) {

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
        if (type == RedisType.Error) {
            val message = incoming.readUTF8Line()
            log.debug { "Received error: $message" }

            throw RedisProtocolException(message)
        }

        log.debug { "Received type: $type" }

        /* read from incoming. */
        return reader.read(type, incoming)
    }

    public suspend fun flush() {
        log.debug { "Flushing incoming stream" }
        incoming.discard()
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

            log.debug { "Sending ${command.name}... ${command.bytes().decodeToString().escaped}" }
            sendPacket(command.packet())

            incoming.awaitContent()

            return readReply(command.reader)
        }
    }
}

public suspend fun RedisClient(uri: String): RedisClient =
    RedisClient(RedisURI(uri))

public suspend fun RedisClient(uri: RedisURI): RedisClient {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("Redis Client"))

    val socket = try {
        aSocket(ActorSelectorManager(scope.coroutineContext))
            .tcp()
            .connect(uri.address)
    } catch (e: Exception) {
        log.error(e) { "Unable to connect to $uri" }
        throw e
    }

    log.info { "Connected to $uri" }

    val client = RedisClient(uri, scope, socket)
    if (uri.auth != null) {
        client.auth(uri.auth)
    }

    return client
}

public inline fun RedisClient.pipelined(build: RedisPipeline.() -> Unit = {}): RedisPipeline {
    contract {
        callsInPlace(build, InvocationKind.EXACTLY_ONCE)
    }

    return RedisPipeline(this)
        .apply { build() }
}
