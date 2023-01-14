package kedis.client

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kedis.annotations.KedisInternalApi
import kedis.client.command.RedisCommand
import kedis.client.command.quit
import kedis.client.command.type.RedisTypeReader
import kedis.exception.RedisProtocolException
import kedis.exception.RedisTypeUnknownException
import kedis.protocol.Protocol
import kedis.protocol.RedisType
import kedis.tools.escaped
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import naibu.logging.logging
import kotlin.jvm.Volatile

@OptIn(InternalCoroutinesApi::class, KedisInternalApi::class)
public open class RedisClient(
    public val uri: RedisURI,
    public val protocol: Protocol,
    private val scope: CoroutineScope,
    private val socket: Socket,
) : SynchronizedObject() {
    public companion object {
        internal val log by logging { }
    }

    internal val mutex: Mutex = Mutex()

    @KedisInternalApi
    public val incoming: ByteReadChannel = socket.openReadChannel()

    @KedisInternalApi
    public val outgoing: ByteWriteChannel = socket.openWriteChannel()

    @Volatile
    private var closing = false

    public val isClosed: Boolean
        get() = closing || !scope.isActive || socket.isClosed

    public suspend fun sendPacket(bytes: ByteArray) {
        sendPacket(ByteReadPacket(bytes))
    }

    public suspend fun sendPacket(packet: ByteReadPacket) {
        outgoing.writePacket(packet)
        outgoing.flush()
    }

    public suspend fun <T> readReply(reader: RedisTypeReader<T>): T? {
        /* validate the type */
        val char = incoming.readByte()
        val type = RedisType.find(char)
            ?: throw RedisTypeUnknownException(char.toInt().toChar())

        /* check if an error was returned. */
        when (type) {
            RedisType.SimpleError -> {
                val message = incoming.readUTF8Line()
                log.trace { "Received error: $message" }

                throw RedisProtocolException(message)
            }

            /* read from incoming. */
            else -> {
                log.trace { "Received type: $type" }

                /* read from incoming. */
                return reader.read(type, this)
            }
        }
    }

    public suspend fun close() {
        if (isClosed) return
        synchronized(this) {
            if (closing) return
            closing = true
        }

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
            require(!socket.isClosed) {
                "The socket for this client has been closed."
            }

            val payload = command.write(protocol.writer).apply {
                log.trace { "Sending ${command.name} -> ${decodeToString().escaped}" }
            }

            outgoing.writePacket(ByteReadPacket(payload))
            outgoing.flush()

            incoming.awaitContent()

            return readReply(command.reader)
        }
    }
}
