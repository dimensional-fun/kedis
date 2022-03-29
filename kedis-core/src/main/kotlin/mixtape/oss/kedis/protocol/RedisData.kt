package mixtape.oss.kedis.protocol

import io.ktor.utils.io.core.*
import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.util.into

public sealed class RedisData(public val type: RedisType) : Rawable {

    public override fun bytes(): ByteArray {
        return byteArrayOf()
    }

    public fun orNull(): RedisData? {
        return if (this is Null) null else this
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

        public fun bytes(forceBulkString: Boolean): ByteArray = RedisProtocolWriter.writeArray(value, forceBulkString)

        override fun toString(): String = "RedisArray[${value.joinToString(", ")}]"
    }

    public open class Integer(public val value: Long) : RedisData(RedisType.Integer) {
        override fun bytes(): ByteArray = RedisProtocolWriter.writeInteger(value)

        override fun toString(): String = "RedisInteger(value=$value)"
    }

    public open class Error(public val value: String) : RedisData(RedisType.Error) {
        override fun bytes(): ByteArray = RedisProtocolWriter.encode(RedisType.Error, value)

        override fun toString(): String = "RedisError(value=$value)"

        public fun yeet(): Nothing {
            throw RedisProtocolException(value)
        }
    }

}

public fun RedisData.asInteger(): Long? {
    return orNull()?.into<RedisData.Integer>()?.value
}

public fun RedisData.asText(): String? {    return orNull()?.into<RedisData.Text>()?.value
}
