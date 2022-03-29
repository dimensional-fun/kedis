package mixtape.oss.kedis.protocol

import io.ktor.utils.io.*
import mixtape.oss.kedis.exception.RedisTypeUnknownException
import mixtape.oss.kedis.exception.notNullable

public object RedisProtocolReader {
    public suspend fun read(channel: ByteReadChannel): RedisData {
        return when (readReplyType(channel)) {
            RedisType.Integer ->
                readIntegerReply(channel)

            RedisType.BulkString ->
                readBulkStringReply(channel)

            RedisType.SimpleString ->
                readSimpleStringReply(channel)

            RedisType.Array ->
                readArray(channel)

            RedisType.Error ->
                readError(channel).yeet()
        }
    }

    public suspend fun readReplyType(channel: ByteReadChannel): RedisType {
        val byte = channel.readByte()

        return RedisType.find(byte) ?: throw RedisTypeUnknownException()
    }

    public suspend fun readSimpleStringReply(channel: ByteReadChannel): RedisData.Text {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to read simple string reply"
        }

        return RedisData.Text(RedisType.SimpleString, header.removePrefix("+"))
    }

    public suspend fun readBulkStringReply(channel: ByteReadChannel): RedisData {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to read header for bulk string reply"
        }

        val length = notNullable(header.removePrefix("$").toLongOrNull()) {
            "Unable to read bulk string reply length"
        }

        if (length == -1L) {
            return RedisData.Null
        }

        val data = channel.readRemaining(length + 2)
        return RedisData.Text(RedisType.BulkString, data.readText().removeSuffix("\r\n"))
    }

    public suspend fun readIntegerReply(channel: ByteReadChannel): RedisData.Integer {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to integer reply header"
        }

        val value = notNullable(header.removePrefix(":").toLongOrNull()) {
            "Reply was not an integer"
        }

        return RedisData.Integer(value)
    }

    public suspend fun readError(channel: ByteReadChannel): RedisData.Error {
        val message = notNullable(channel.readUTF8Line()) {
            "Unable to read error reply"
        }

        return RedisData.Error(message)
    }

    public suspend fun readArray(channel: ByteReadChannel): RedisData.Array {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to read list reply header"
        }

        val size = notNullable(header.removePrefix("*").toIntOrNull()) {
            "Array size was not an integer"
        }

        val elements = List(size) {
            when (readReplyType(channel)) {
                RedisType.BulkString -> readBulkStringReply(channel)
                RedisType.SimpleString -> readSimpleStringReply(channel)
                RedisType.Integer -> readIntegerReply(channel)
                RedisType.Array -> readArray(channel)
                RedisType.Error -> readError(channel)
            }
        }

        val errors = elements.filterIsInstance<RedisData.Error>()
        if (errors.isNotEmpty()) {
            errors.first().yeet()
        }

        return RedisData.Array(elements)
    }
}
