package kedis.protocol.resp2

import kedis.protocol.Rawable
import kedis.protocol.RedisData
import kedis.protocol.RedisProtocolWriter
import kedis.protocol.RedisType
import kedis.tools.CRLF

public object Resp2ProtocolWriter : RedisProtocolWriter {
    public override fun write(value: Any?, terminate: Boolean, forceBulkString: Boolean): ByteArray {
        return when (value) {
            null -> writeNull(terminate)
            is Float, is Double, is String -> if (forceBulkString) {
                writeBulkString(value.toString(), terminate)
            } else {
                writeString(value.toString(), terminate)
            }
            is Int -> writeInteger(value.toLong(), terminate)
            is Long -> writeInteger(value, terminate)
            is Boolean -> writeInteger(if (value) 1 else 0, terminate)
            is Collection<*> -> writeArray(value)
            is Rawable -> value.bytes()
            is RedisData -> value.write(Resp2ProtocolWriter)
            else -> error("Unable to compose value: $value")
        }
    }

    public override fun writeNull(terminate: Boolean): ByteArray =
        encode(RedisType.BulkString, -1, terminate = terminate)

    override fun writeSimpleError(message: String, terminate: Boolean): ByteArray =
        encode(RedisType.SimpleError, message, terminate = terminate)

    /* string */
    public override fun writeSimpleString(str: String, terminate: Boolean): ByteArray =
        encode(RedisType.SimpleString, str, terminate = terminate)

    public override fun writeBulkString(str: String, terminate: Boolean): ByteArray =
        encode(RedisType.BulkString, str.length, CRLF, str, terminate = terminate)

    /* integer */
    public override fun writeInteger(value: Long, terminate: Boolean): ByteArray =
        encode(RedisType.Integer, value, terminate = terminate)

    /* arrays */
    public override fun writeArray(value: Collection<*>, forceBulkString: Boolean): ByteArray =
        encode(
            RedisType.Array,
            value.size,
            CRLF,
            *value.map { write(it, true, forceBulkString) }.toTypedArray(),
            terminate = value.isEmpty()
        )

    /* utils */
    public fun toString(value: Any): String? {
        return when (value) {
            is Number -> value.toString()
            is String -> value
            is Boolean -> if (value) "1" else "0"
            else -> null
        }
    }

    /* RESP3 */
    override fun writeDouble(value: Double, terminate: Boolean): ByteArray {
        TODO("Not yet implemented")
    }

    override fun writeFloat(value: Float, terminate: Boolean): ByteArray {
        TODO("Not yet implemented")
    }

    private fun encode(vararg args: Any, terminate: Boolean = true): ByteArray {
        val bytes = join(*args)
        return if (terminate) bytes + CRLF else bytes
    }

    private fun join(vararg args: Any): ByteArray {
        return args
            .map {
                when (it) {
                    is Rawable -> it.bytes()
                    is ByteArray -> it
                    else -> toString(it)?.encodeToByteArray()
                        ?: error("Unable to encode $it to a byte array")
                }
            }
            .fold(byteArrayOf()) { a, b -> a + b }
    }
}
