package mixtape.oss.kedis.command.group

import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.type.RedisTypeReader
import mixtape.oss.kedis.util.doIf

public interface HashCommands {
    public fun hdel(key: String, field: String, vararg fields: String): RedisCommand<Long> =
        RedisCommand("HDEL", RedisTypeReader.Long, key, field, *fields)

    public fun hexists(key: String, field: String): RedisCommand<Boolean> =
        RedisCommand("HEXISTS", RedisTypeReader.Boolean, key, field)

    public fun hget(key: String, field: String): RedisCommand<String> =
        RedisCommand("HGET", RedisTypeReader.BulkString, key, field)

    public fun hgetAll(key: String): RedisCommand<Map<String, String>> =
        RedisCommand("HGET", RedisTypeReader.StringMap, key)

    public fun hincrBy(key: String, field: String, by: Long): RedisCommand<Long> =
        RedisCommand("HINCRBY", RedisTypeReader.Long, key, field, by)

    public fun hincrByFloat(key: String, field: String, by: Float): RedisCommand<Long> =
        RedisCommand("HINCRBYFLOAT", RedisTypeReader.Long, key, field, by)

    public fun hkeys(key: String): RedisCommand<List<String?>> =
        RedisCommand("HKEYS", RedisTypeReader.StringList, key)

    public fun hlen(key: String): RedisCommand<Long> =
        RedisCommand("HLEN", RedisTypeReader.Long, key)

    public fun hmget(key: String, field: String, vararg fields: String): RedisCommand<List<String?>> =
        RedisCommand("HMGET", RedisTypeReader.StringList, key, field, *fields)

    @Deprecated("As of Redis 4.0.0 this is deprecated", ReplaceWith("hset"))
    public fun hmset(key: String, fields: Map<String, Any>): RedisCommand<List<String?>> =
        RedisCommand("HMSET", RedisTypeReader.StringList, key, *fields.flatMap { (k, v) -> listOf(k, v) }.toTypedArray())

    public fun hrandfield(key: String): RedisCommand<String> =
        RedisCommand("HRANDFIELD", RedisTypeReader.BulkString, key)

    public fun hrandfield(key: String, count: Long, withValues: Boolean = false): RedisCommand<List<String?>> =
        RedisCommand("HRANDFIELD", RedisTypeReader.StringListOrBulkString, key, count)
            .doIf(withValues) { withArg("WITHVALUES") }

    public fun hscan(key: String, cursor: Int, pattern: String? = null, count: Long? = null): RedisCommand<String> =
        RedisCommand("HRANDFIELD", RedisTypeReader.BulkString, key)
            .doIf(pattern != null) { withOption("PATTERN", pattern!!) }
            .doIf(count != null) { withOption("COUNT", count!!) }

    public fun hset(key: String, fields: Map<String, Any>): RedisCommand<List<String?>> =
        RedisCommand("HSET", RedisTypeReader.StringList, key, *fields.flatMap { (k, v) -> listOf(k, v) }.toTypedArray())

    public fun hsetnx(key: String, field: String, value: Any): RedisCommand<Boolean> =
        RedisCommand("HSETNX", RedisTypeReader.Boolean, key, field, value)

    public fun hstrlen(key: String, field: String): RedisCommand<Long> =
        RedisCommand("HSTRLEN", RedisTypeReader.Long, key, field)

    public fun hvals(key: String): RedisCommand<Long> =
        RedisCommand("HVALS", RedisTypeReader.Long, key)
}
