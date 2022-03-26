package mixtape.oss.kedis.protocol

import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.exception.RedisTypeUnknownException
import mu.KotlinLogging

public object RedisProtocolReader {
    public suspend fun readReplyType(channel: ByteReadChannel): RedisType {
        val byte = channel.readByte()

        return RedisType.find(byte) ?: throw RedisTypeUnknownException()
    }

    public suspend fun readSimpleStringReply(channel: ByteReadChannel): String {
        val header = channel.readUTF8Line()
            ?: throw RedisProtocolException("Unable to read simple string reply")

        return header.removePrefix("+")
    }

    public suspend fun readBulkStringReply(channel: ByteReadChannel): String? {
        val header = channel.readUTF8Line()
            ?: throw RedisProtocolException("Unable to read header for bulk string reply")

        val length = header.removePrefix("$").toLongOrNull()
            ?: throw RedisProtocolException("Unable to read bulk string reply length")

        if (length == -1L) {
            return null
        }

        val data = channel.readRemaining(length + 2)
        return data.readText().removeSuffix("\r\n")
    }

    public suspend fun readIntegerReply(channel: ByteReadChannel): Long {
        val header = channel.readUTF8Line()
            ?: throw RedisProtocolException("Unable to integer reply header")

        return header.removePrefix(":").toLongOrNull()
            ?: throw RedisProtocolException("Reply was not an integer")
    }

    public suspend fun readArray(channel: ByteReadChannel): List<RedisData> {
        val header = channel.readUTF8Line()
            ?: throw RedisProtocolException("Unable to read list reply header")

        val size = header.removePrefix("*").toIntOrNull()
            ?: throw RedisProtocolException("Array size was not an integer")

        return List(size) {
            when (readReplyType(channel)) {
                RedisType.BulkString -> readBulkStringReply(channel)?.let { RedisData.Text(RedisType.BulkString, it) } ?: RedisData.Null
                RedisType.SimpleString -> RedisData.Text(RedisType.SimpleString, readSimpleStringReply(channel))
                RedisType.Integer -> RedisData.Integer(readIntegerReply(channel))
                RedisType.Array -> RedisData.Array(readArray(channel))
                RedisType.Error -> throw RedisProtocolException(channel.readUTF8Line())
            }
        }
    }
}
