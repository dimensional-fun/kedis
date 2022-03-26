package mixtape.oss.kedis.command

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import mixtape.oss.kedis.exception.RedisProtocolException
import mixtape.oss.kedis.protocol.RedisData
import mixtape.oss.kedis.protocol.RedisProtocolReader
import mixtape.oss.kedis.protocol.RedisType
import mixtape.oss.kedis.util.into

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
            if (t == RedisType.BulkString) {
                RedisProtocolReader.readBulkStringReply(c)
            } else {
                RedisProtocolReader.readSimpleStringReply(c)
            }
        }

        public val StringList: RedisTypeReader<List<String>> = RedisTypeReader(RedisType.Array) { t, c ->
            RedisProtocolReader.readArray(c)
                .map { it.into<RedisData.Text>() }
                .map { it.value }
        }

        public val LongList: RedisTypeReader<List<Long>> = RedisTypeReader(RedisType.Array) { t, c ->
            RedisProtocolReader.readArray(c)
                .map { it.into<RedisData.Integer>() }
                .map { it.value }
        }

        public val SimpleString: RedisTypeReader<String> = RedisTypeReader(RedisType.SimpleString) { _, c ->
            RedisProtocolReader.readSimpleStringReply(c)
        }

        public val BulkString: RedisTypeReader<String> = RedisTypeReader(RedisType.BulkString) { _, c ->
            RedisProtocolReader.readBulkStringReply(c)
        }

        public val Boolean: RedisTypeReader<Boolean> = RedisTypeReader(RedisType.BulkString) { _, c ->
            RedisProtocolReader.readIntegerReply(c) == 1L
        }

        public val Long: RedisTypeReader<Long> = RedisTypeReader(RedisType.Integer) { _, c ->
            RedisProtocolReader.readIntegerReply(c)
        }
    }

    public suspend fun read(type: RedisType, channel: ByteReadChannel): T?
}
