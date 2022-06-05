package mixtape.oss.kedis.protocol.resp2

import io.ktor.utils.io.*
import mixtape.oss.kedis.exception.notNullable
import mixtape.oss.kedis.protocol.RedisData
import mixtape.oss.kedis.protocol.RedisProtocolReader
import mixtape.oss.kedis.protocol.RedisType
import mixtape.oss.kedis.util.removePrefix

public object Resp2ProtocolReader : RedisProtocolReader {
    override val supports: List<RedisType>
        get() = listOf(
            RedisType.Integer,
            RedisType.BulkString,
            RedisType.SimpleString,
            RedisType.Array,
            RedisType.SimpleError
        )

    override suspend fun read(channel: ByteReadChannel): RedisData {
        return when (val type = readReplyType(channel)) {
            RedisType.Integer -> readIntegerReply(channel)
            RedisType.BulkString -> readBulkStringReply(channel)
            RedisType.SimpleString -> readSimpleStringReply(channel)
            RedisType.Array -> readArray(channel)
            RedisType.SimpleError -> readSimpleError(channel).yeet()
        }
    }

    override suspend fun readSimpleStringReply(channel: ByteReadChannel): RedisData.Text {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to read simple string reply"
        }

        return RedisData.Text(RedisType.SimpleString, header.removePrefix(RedisType.SimpleString))
    }

    override suspend fun readBulkStringReply(channel: ByteReadChannel): RedisData {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to read header for bulk string reply"
        }

        val length = notNullable(header.removePrefix(RedisType.BulkString).toLongOrNull()) {
            "Unable to read bulk string reply length"
        }

        if (length == -1L) {
            return RedisData.Null
        }

        val packet = channel.readRemaining(length)
        channel.discard(2) // discarding CRLF

        return RedisData.Text(RedisType.BulkString, packet.readText())
    }

    override suspend fun readIntegerReply(channel: ByteReadChannel): RedisData.Integer {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to integer reply header"
        }

        val value = notNullable(header.removePrefix(RedisType.Integer).toLongOrNull()) {
            "Reply was not an integer"
        }

        return RedisData.Integer(value)
    }

    override suspend fun readSimpleError(channel: ByteReadChannel): RedisData.Error {
        val message = notNullable(channel.readUTF8Line()) {
            "Unable to read error reply"
        }

        return RedisData.Error(message)
    }
}
