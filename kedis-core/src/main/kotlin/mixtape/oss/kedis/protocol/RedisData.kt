package mixtape.oss.kedis.protocol

import io.ktor.utils.io.core.*

public sealed class RedisData(public val type: RedisType) : Rawable {

    override val raw: ByteArray
        get() = bytes()

    public open fun bytes(): ByteArray {
        return byteArrayOf()
    }

    public open fun packet(): ByteReadPacket {
        return ByteReadPacket(bytes())
    }

    public object Null : RedisData(RedisType.BulkString) {
        override fun bytes(): ByteArray = RedisProtocolWriter.writeNull()

        override fun toString(): String = "RedisNull"
    }

    public open class Text(type: RedisType, public val value: String) : RedisData(type) {
        override fun bytes(): ByteArray {
            return when (type) {
                RedisType.SimpleString -> RedisProtocolWriter.writeSimpleString(value)
                RedisType.BulkString -> RedisProtocolWriter.writeBulkString(value)
                else -> error("Invalid text redis type.")
            }
        }

        override fun toString(): String = "RedisText(type=$type, value=$value)"
    }

    public open class Array(public val value: List<RedisData>) : RedisData(RedisType.Array) {
        override fun bytes(): ByteArray = RedisProtocolWriter.writeArray(value)

        override fun toString(): String = "RedisArray[${value.joinToString(", ")}]"
    }

    public open class Integer(public val value: Long) : RedisData(RedisType.Integer) {
        override fun bytes(): ByteArray = RedisProtocolWriter.writeInteger(value)

        override fun toString(): String = "RedisInteger(value=$value)"
    }

}
