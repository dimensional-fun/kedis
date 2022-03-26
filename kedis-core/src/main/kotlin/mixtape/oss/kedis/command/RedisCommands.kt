package mixtape.oss.kedis.command

public object RedisCommands {
    public val ping: RedisCommand<String> = RedisCommand("PING", RedisTypeReader.SimpleString)

    public val info: RedisCommand<String> = RedisCommand("INFO", RedisTypeReader.BulkString)

    public fun del(vararg keys: String): RedisCommand<Long> =
        RedisCommand("DEL", RedisTypeReader.Long, *keys)

    public fun del(keys: List<String>): RedisCommand<Long> =
        RedisCommand("DEL", RedisTypeReader.Long, keys)

    public fun exists(key: String): RedisCommand<Boolean> =
        RedisCommand("EXISTS", RedisTypeReader.Boolean, key)

    public fun copy(source: String, destination: String): RedisCommand<Boolean> =
        RedisCommand("COPY", RedisTypeReader.Boolean, source, destination)
}
