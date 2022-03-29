package mixtape.oss.kedis.protocol

import mixtape.oss.kedis.util.containsCR
import mixtape.oss.kedis.util.containsLF
import mixtape.oss.kedis.util.CRLF

public object RedisProtocolWriter {
    public fun write(value: Any?, terminate: Boolean = true, forceBulkString: Boolean = false): ByteArray {
        return when(value) {
            null -> writeNull(terminate)
            is String -> if (forceBulkString) writeBulkString(value, terminate) else writeString(value, terminate)
            is Int -> writeInteger(value.toLong(), terminate)
            is Long -> writeInteger(value, terminate)
            is Boolean -> writeInteger(if (value) 1 else 0, terminate)
            is Collection<*> -> writeArray(value)
            is Rawable -> value.bytes()
            else -> error("Unable to compose value: $value")
        }
    }

    public fun writeNull(terminate: Boolean = true): ByteArray =
        encode(RedisType.BulkString, -1, terminate = terminate)

    /* string */
    public fun writeString(string: String, terminate: Boolean = true): ByteArray =
        if (string.containsLF() || string.containsCR()) writeBulkString(string, terminate) else writeSimpleString(string, terminate)

    public fun writeSimpleString(str: String, terminate: Boolean = true): ByteArray =
        encode(RedisType.SimpleString, str, terminate = terminate)

    public fun writeBulkString(str: String, terminate: Boolean = true): ByteArray =
        encode(RedisType.BulkString, str.length, CRLF, str, terminate = terminate)

    /* integer */
    public fun writeInteger(value: Long, terminate: Boolean = true): ByteArray =
        encode(RedisType.Integer, value, terminate = terminate)

    /* arrays */
    public fun writeArray(value: Collection<*>, forceBulkString: Boolean = false): ByteArray =
        encode(RedisType.Array, value.size, CRLF, *value.map { write(it, true, forceBulkString) }.toTypedArray(), terminate = value.isEmpty())

    /* utils */
    public fun toString(value: Any): String? {
        return when (value) {
            is Int -> value.toString()
            is Long -> value.toString()
            is String -> value
            is Boolean -> if (value) "1" else "0"
            else -> null
        }
    }

    public fun encode(vararg args: Any, terminate: Boolean = true): ByteArray {
        val bytes = join(*args)
        return if (terminate) bytes + CRLF else bytes
    }

    public fun join(vararg args: Any): ByteArray {
        return args
            .map {
                when (it) {
                    is Rawable -> it.bytes()
                    is ByteArray -> it
                    else -> toString(it)?.encodeToByteArray() ?: error("Unable to encode $it to a byte array")
                }
            }
            .fold(byteArrayOf()) { a, b -> a + b }
    }
}
