package mixtape.oss.kedis.protocol

import mixtape.oss.kedis.util.containsCR
import mixtape.oss.kedis.util.containsLF

public interface RedisProtocolWriter {
    public fun write(value: Any?, terminate: Boolean = true, forceBulkString: Boolean = false): ByteArray

    public fun writeNull(terminate: Boolean = true): ByteArray

    public fun writeSimpleError(message: String, terminate: Boolean = true): ByteArray

    public fun writeString(string: String, terminate: Boolean = true): ByteArray {
        return if (string.containsLF() || string.containsCR()) {
            writeBulkString(string, terminate)
        } else {
            writeSimpleString(string, terminate)
        }
    }

    public fun writeSimpleString(str: String, terminate: Boolean = true): ByteArray

    public fun writeBulkString(str: String, terminate: Boolean = true): ByteArray

    public fun writeInteger(value: Long, terminate: Boolean = true): ByteArray

    public fun writeDouble(value: Double, terminate: Boolean = true): ByteArray

    public fun writeFloat(value: Float, terminate: Boolean = true): ByteArray

    public fun writeArray(value: Collection<*>, forceBulkString: Boolean = false): ByteArray
}
