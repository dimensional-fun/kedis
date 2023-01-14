@file:OptIn(KedisInternalApi::class)

package kedis.client.command.type

import kedis.client.RedisClient
import kedis.annotations.KedisInternalApi
import kedis.exception.RedisProtocolException
import kedis.protocol.RedisData
import kedis.protocol.RedisType
import kedis.protocol.asInteger
import kedis.protocol.asText
import kedis.protocol.resp2.Resp2ProtocolReader

public fun interface RedisTypeReader<T> {
    public companion object {
        public operator fun <T> invoke(
            vararg types: RedisType,
            block: suspend (RedisType, RedisClient) -> T?,
        ): RedisTypeReader<T> =
            RedisTypeReader { t, c ->
                if (!types.contains(t)) {
                    throw RedisProtocolException("Return type of command was $t instead of ${types.joinToString()}")
                } else {
                    block(t, c)
                }
            }

        public val String: RedisTypeReader<String> =
            RedisTypeReader(RedisType.SimpleString, RedisType.BulkString) { t, c ->
                val value = when (t) {
                    RedisType.BulkString ->
                        c.protocol.reader.readBulkStringReply(c.incoming)
                    else ->
                        c.protocol.reader.readSimpleStringReply(c.incoming)
                }

                value.asText()
            }

        public val Map: RedisTypeReader<Map<String, RedisData>> = RedisTypeReader(RedisType.Array) { t, c ->
            val array = Resp2ProtocolReader.readArray(c.incoming).value
            array.chunked(2).associate { (k, v) -> k.asText()!! to v }
        }

        public val StringMap: RedisTypeReader<Map<String, String>> = Map then { _, _, c ->
            c.mapValues { it.value.asText()!! }
        }

        // TODO: maybe make like an inner type for array
        public val Unknown: RedisTypeReader<RedisData> = RedisTypeReader { _, c ->
            c.protocol.reader.read(c.incoming)
        }

        public val UnknownList: RedisTypeReader<RedisData.Array> = RedisTypeReader { _, c ->
            c.protocol.reader.readArray(c.incoming)
        }

        public val StringList: RedisTypeReader<List<String?>> = RedisTypeReader(RedisType.Array) { _, c ->
            c.protocol.reader.readArray(c.incoming).value.map { it.asText() }
        }

        public val StringListOrBulkString: RedisTypeReader<List<String?>> =
            RedisTypeReader(RedisType.Array, RedisType.BulkString) { t, c ->
                if (t == RedisType.Array) {
                    StringList.read(t, c)
                } else {
                    listOf(BulkString.read(t, c))
                }
            }

        public val LongList: RedisTypeReader<List<Long?>> = RedisTypeReader(RedisType.Array) { t, c ->
            c.protocol.reader.readArray(c.incoming).value.map { it.asInteger() }
        }

        public val SimpleString: RedisTypeReader<String> = RedisTypeReader(RedisType.SimpleString) { _, c ->
            c.protocol.reader.readSimpleStringReply(c.incoming).value
        }

        public val BulkString: RedisTypeReader<String> = RedisTypeReader(RedisType.BulkString) { _, c ->
            c.protocol.reader.readBulkStringReply(c.incoming).asText()
        }

        public val Boolean: RedisTypeReader<Boolean> = RedisTypeReader(RedisType.Integer) { _, c ->
            c.protocol.reader.readIntegerReply(c.incoming).value == 1L
        }

        public val Long: RedisTypeReader<Long> = RedisTypeReader(RedisType.Integer) { _, c ->
            c.protocol.reader.readIntegerReply(c.incoming).value
        }
    }

    public suspend fun read(type: RedisType, client: RedisClient): T?

    public infix fun <R> then(block: suspend (RedisType, RedisClient, T) -> R?): RedisTypeReader<R> =
        RedisTypeReader { type, channel ->
            val returned = read(type, channel)
            returned?.let { block(type, channel, returned) }
        }
}
