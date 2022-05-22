package mixtape.oss.kedis.protocol

import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.util.into

public sealed class RedisData : Writable {
    override fun write(writer: RedisProtocolWriter): ByteArray {
        return byteArrayOf()
    }

    public fun orNull(): RedisData? {
        return if (this is Null) null else this
    }

    public object Null : RedisData() {
        override fun write(writer: RedisProtocolWriter): ByteArray = writer.writeNull()

        override fun toString(): String = "RedisNull"
    }

    public open class Text(public val type: RedisType, public val value: String) : RedisData() {
        override fun write(writer: RedisProtocolWriter): ByteArray {
            return when (type) {
                RedisType.SimpleString -> writer.writeSimpleString(value)
                RedisType.BulkString -> writer.writeBulkString(value)
                else -> error("Invalid text redis type.")
            }
        }

        override fun toString(): String = "RedisText(type=$type, value=$value)"
    }

    public open class Array(public val value: List<RedisData>) : RedisData() {
        override fun write(writer: RedisProtocolWriter): ByteArray =
            writer.writeArray(value)

        public fun write(writer: RedisProtocolWriter, forceBulkString: Boolean): ByteArray =
            writer.writeArray(value, forceBulkString)

        override fun toString(): String = "RedisArray[${value.joinToString(", ")}]"
    }

    public open class Integer(public val value: Long) : RedisData() {
        override fun write(writer: RedisProtocolWriter): ByteArray = writer.writeInteger(value)

        override fun toString(): String = "RedisInteger(value=$value)"
    }

    public open class Bool(public val value: Boolean) : RedisData() {
        override fun write(writer: RedisProtocolWriter): ByteArray = writer.write(value)

        override fun toString(): String = "RedisBool(value=$value)"
    }

    public open class Error(public val value: String) : RedisData() {
        override fun write(writer: RedisProtocolWriter): ByteArray = writer.writeSimpleString(value)

        override fun toString(): String = "RedisError(value=$value)"

        public fun yeet(): Nothing = throw RedisProtocolException(value)
    }
}

public fun RedisData.asInteger(): Long? = orNull()?.into<RedisData.Integer>()?.value

public fun RedisData.asText(): String? = orNull()?.into<RedisData.Text>()?.value

public fun RedisData.asArray(): List<RedisData>? = orNull()?.into<RedisData.Array>()?.value
