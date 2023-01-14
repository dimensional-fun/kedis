package kedis.protocol

import io.ktor.utils.io.*
import kedis.exception.RedisTypeUnknownException
import kedis.exception.notNullable
import kedis.tools.removePrefix

public interface RedisProtocolReader {
    public val supports: List<RedisType>

    public suspend fun read(channel: ByteReadChannel): RedisData

    public suspend fun readReplyType(channel: ByteReadChannel): RedisType {
        val byte = channel.readByte()

        return RedisType.find(byte) ?: throw RedisTypeUnknownException(byte.toInt().toChar())
    }

    /* shared types */
    public suspend fun readSimpleStringReply(channel: ByteReadChannel): RedisData.Text

    public suspend fun readBulkStringReply(channel: ByteReadChannel): RedisData

    public suspend fun readIntegerReply(channel: ByteReadChannel): RedisData.Integer

    public suspend fun readSimpleError(channel: ByteReadChannel): RedisData.Error

    public suspend fun readArray(channel: ByteReadChannel): RedisData.Array {
        val header = notNullable(channel.readUTF8Line()) {
            "Unable to read list reply header"
        }

        val size = notNullable(header.removePrefix(RedisType.Array).toIntOrNull()) {
            "Array size was not an integer"
        }

        return RedisData.Array(List(size) { read(channel) })
    }
}
