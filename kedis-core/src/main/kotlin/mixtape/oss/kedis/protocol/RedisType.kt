package mixtape.oss.kedis.protocol

public enum class RedisType(public val char: Char) : Rawable {
    SimpleString('+'),
    BulkString('$'),
    Error('-'),
    Integer(':'),
    Array('*');

    override fun bytes(): ByteArray = byteArrayOf(char.code.toByte())

    public companion object {
        public fun find(byte: Byte): RedisType? = values().find { it.char.code.toByte() == byte }

        public fun find(char: Char): RedisType? = values().find { it.char == char }
    }
}
