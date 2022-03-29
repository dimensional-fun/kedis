package mixtape.oss.kedis.command

import io.ktor.utils.io.*
import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.protocol.*

public fun interface RedisTypeReader<T> {
    public companion object {
        public operator fun <T> invoke(vararg types: RedisType, block: suspend (RedisType, ByteReadChannel) -> T?): RedisTypeReader<T> =
            RedisTypeReader { t, c ->
                if (!types.contains(t)) {
                    throw RedisProtocolException("Return type of command was $t instead of ${types.joinToString()}")
                } else {
                    block(t, c)
                }
            }

        public val String: RedisTypeReader<String> = RedisTypeReader(RedisType.SimpleString, RedisType.BulkString) { t, c ->
            val value = if (t == RedisType.BulkString) {
                RedisProtocolReader.readBulkStringReply(c)
            } else {
                RedisProtocolReader.readSimpleStringReply(c)
            }

            value.asText()
        }

        // TODO: maybe make like an inner type for array
        public val Unknown: RedisTypeReader<RedisData> = RedisTypeReader { t, c ->
            RedisProtocolReader.read(c)
        }

        public val StringList: RedisTypeReader<List<String?>> = RedisTypeReader(RedisType.Array) { t, c ->
            RedisProtocolReader.readArray(c).value.map { it.asText() }
        }

        public val LongList: RedisTypeReader<List<Long?>> = RedisTypeReader(RedisType.Array) { t, c ->
            RedisProtocolReader.readArray(c).value.map { it.asInteger() }
        }

        public val SimpleString: RedisTypeReader<String> = RedisTypeReader(RedisType.SimpleString) { _, c ->
            RedisProtocolReader.readSimpleStringReply(c).value
        }

        public val BulkString: RedisTypeReader<String> = RedisTypeReader(RedisType.BulkString) { _, c ->
            RedisProtocolReader.readBulkStringReply(c).asText()
        }

        public val Boolean: RedisTypeReader<Boolean> = RedisTypeReader(RedisType.Integer) { _, c ->
            RedisProtocolReader.readIntegerReply(c).value == 1L
        }

        public val Long: RedisTypeReader<Long> = RedisTypeReader(RedisType.Integer) { _, c ->
            RedisProtocolReader.readIntegerReply(c).value
        }
    }

    public suspend fun read(type: RedisType, channel: ByteReadChannel): T?
}
