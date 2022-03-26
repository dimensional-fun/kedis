package mixtape.oss.kedis

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import mixtape.oss.kedis.util.escaped
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.RedisTypeReader
import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.exception.RedisTypeUnknownException
import mixtape.oss.kedis.protocol.RedisType
import mu.KotlinLogging

public class RedisClientImpl(override val uri: RedisURI, private val scope: CoroutineScope, private val socket: Socket) : RedisClient {

    private val incoming: ByteReadChannel = socket.openReadChannel()
    private val outgoing: ByteWriteChannel = socket.openWriteChannel(true)

    public val isClosed: Boolean
        get() = !scope.isActive || socket.isClosed

    override suspend fun <T> sendCommand(command: RedisCommand<T>): T? {
        require (!socket.isClosed) {
            "The socket for this client has been closed."
        }

        log.info { "Sending ${command.name}... ${command.bytes().decodeToString().escaped}" }
        outgoing.writePacket(command.packet())

        /* read the next packet */
        incoming.awaitContent()

        /* validate the type */
        val type = RedisType.find(incoming.readByte())
            ?: throw RedisTypeUnknownException()

        /* check if an error was returned. */
        if (type == RedisType.Error) {
            val message = incoming.readUTF8Line()
            log.debug { "Received error: $message" }

            throw RedisProtocolException(message)
        }

        log.info { "Received type: $type" }

        /* read from incoming. */
        return command.reader.read(type, incoming)
    }

    override suspend fun close() {
       try {
           sendCommand(RedisCommand("QUIT", RedisTypeReader.SimpleString))
           socket.dispose()
           scope.cancel()
           log.debug { "Client has been closed." }
       } catch (ex: Exception) {
           log.error(ex) { "Unable to cleanly close redis connection:" }
       }
    }

    public override suspend fun authenticate(auth: RedisAuth) {
        sendCommand(if (auth.username.isNullOrBlank()) {
            RedisCommand("AUTH", RedisTypeReader.SimpleString, auth.password)
        } else {
            RedisCommand("AUTH", RedisTypeReader.SimpleString, auth.username, auth.password)
        })
    }
}
