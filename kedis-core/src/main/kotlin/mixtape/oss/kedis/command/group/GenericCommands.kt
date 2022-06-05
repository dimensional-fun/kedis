package mixtape.oss.kedis.command.group

import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.type.ExistenceModifier
import mixtape.oss.kedis.command.type.ExpireOption
import mixtape.oss.kedis.command.type.KeyExpiry
import mixtape.oss.kedis.command.type.RedisTypeReader
import mixtape.oss.kedis.protocol.RedisType

private val setReader = RedisTypeReader<Any?>(RedisType.SimpleString, RedisType.BulkString) { type, client ->
    return@RedisTypeReader when (type) {
        RedisType.BulkString -> RedisTypeReader.BulkString.read(type, client)
        RedisType.SimpleString -> {
            RedisTypeReader.SimpleString.read(type, client)
            true
        }
        else -> throw IllegalStateException()
    }
}

public interface GenericCommands {
    public companion object {
        public const val NO_KEY_EXPIRY: Long = -1
        public const val NO_KEY_FOUND: Long = -2
    }

    public fun copy(source: String, destination: String): RedisCommand<Boolean> =
        RedisCommand("COPY", RedisTypeReader.Boolean, source, destination)

    public fun del(key: String, vararg keys: String): RedisCommand<Long> =
        RedisCommand("DEL", RedisTypeReader.Long, key, *keys)

    public fun del(keys: Collection<String>): RedisCommand<Long> =
        RedisCommand("DEL", RedisTypeReader.Long, *keys.toTypedArray())

    public fun dump(key: String): RedisCommand<String> =
        RedisCommand("DUMP", RedisTypeReader.BulkString, key)

    public fun exists(key: String): RedisCommand<Boolean> =
        RedisCommand("EXISTS", RedisTypeReader.Boolean, key)

    public fun expire(key: String, seconds: Long): RedisCommand<Long> =
        RedisCommand("EXPIRE", RedisTypeReader.Long, key, seconds.toString())

    public fun expire(key: String, seconds: Long, expiry: ExpireOption): RedisCommand<Long> =
        RedisCommand("EXPIRE", RedisTypeReader.Long, key, seconds.toString(), expiry)

    public fun expireAt(key: String, timestampSeconds: Long): RedisCommand<Long> =
        RedisCommand("EXPIREAT", RedisTypeReader.Long, key, timestampSeconds.toString())

    public fun expireAt(key: String, timestampSeconds: Long, expiry: ExpireOption): RedisCommand<Long> =
        RedisCommand("EXPIREAT", RedisTypeReader.Long, key, timestampSeconds.toString(), expiry)

    public fun expireTime(key: String): RedisCommand<Long> =
        RedisCommand("EXPIRETIME", RedisTypeReader.Long, key)

    public fun keys(pattern: String): RedisCommand<List<String?>> =
        RedisCommand("KEYS", RedisTypeReader.StringList, pattern)

    public fun move(key: String, database: Int): RedisCommand<Long> =
        RedisCommand("MOVE", RedisTypeReader.Long, key, database)

    public fun objectEncoding(key: String): RedisCommand<String> =
        RedisCommand("OBJECT", RedisTypeReader.BulkString, "ENCODING", key)

    public fun objectFreq(key: String): RedisCommand<Long> =
        RedisCommand("OBJECT", RedisTypeReader.Long, "FREQ", key)

    public fun objectHelp(): RedisCommand<List<String?>> =
        RedisCommand("OBJECT", RedisTypeReader.StringList, "HELP")

    public fun objectIdleTime(key: String): RedisCommand<Long> =
        RedisCommand("OBJECT", RedisTypeReader.Long, "IDLETIME", key)

    public fun objectRefCount(key: String): RedisCommand<Long> =
        RedisCommand("OBJECT", RedisTypeReader.Long, "REFCOUNT", key)

    public fun persist(key: String): RedisCommand<Long> =
        RedisCommand("PERSIST", RedisTypeReader.Long, key)

    public fun pexpire(key: String, milliseconds: Long): RedisCommand<Long> =
        RedisCommand("PEXPIRE", RedisTypeReader.Long, key, milliseconds)

    public fun pexpire(key: String, milliseconds: Long, expiry: ExpireOption): RedisCommand<Long> =
        RedisCommand("PEXPIRE", RedisTypeReader.Long, key, milliseconds, expiry)

    public fun pexpireAt(key: String, timestampMillis: Long): RedisCommand<Long> =
        RedisCommand("PEXPIREAT", RedisTypeReader.Long, key, timestampMillis)

    public fun pexpireAt(key: String, timestampMillis: Long, expiry: ExpireOption): RedisCommand<Long> =
        RedisCommand("PEXPIREAT", RedisTypeReader.Long, key, timestampMillis, expiry)

    public fun pexpireTime(key: String): RedisCommand<Long> =
        RedisCommand("PEXPIRETIME", RedisTypeReader.Long, key)

    public fun pttl(key: String): RedisCommand<Long> =
        RedisCommand("PTTL", RedisTypeReader.Long, key)

    public fun randomKey(key: String): RedisCommand<String> =
        RedisCommand("RANDOMKEY", RedisTypeReader.BulkString)

    public fun rename(key: String, newkey: String): RedisCommand<String> =
        RedisCommand("RENAME", RedisTypeReader.SimpleString, key, newkey)

    public fun renameNX(key: String, newkey: String): RedisCommand<Boolean> =
        RedisCommand("RENAMENX", RedisTypeReader.Boolean, key, newkey)

    /*public fun restore(key: String, ttl: Long, serializedValue: String, ): RedisCommand<String> =
        RedisCommand("RENAMENX", RedisTypeReader.SimpleString, key, ttl, serializedValue)*/

    /*public fun scan(cursor: Long, )*/

    @Suppress("UNCHECKED_CAST")
    public fun set(
        key: String,
        value: String,
        existenceModifier: ExistenceModifier? = null,
        expiry: KeyExpiry? = null
    ): RedisCommand<Boolean> =
        set(key, value, existenceModifier, false, expiry) as RedisCommand<Boolean>

    @Suppress("UNCHECKED_CAST")
    public fun setGet(
        key: String,
        value: String,
        existenceModifier: ExistenceModifier? = null,
        expiry: KeyExpiry? = null
    ): RedisCommand<String> =
        set(key, value, existenceModifier, true, expiry) as RedisCommand<String>

    private fun set(
        key: String,
        value: String,
        existenceModifier: ExistenceModifier? = null,
        get: Boolean = false,
        expiry: KeyExpiry? = null
    ): RedisCommand<Any?> {
        val options = ArrayList<RedisCommand.Option>()
        if (existenceModifier != null) {
            options.add(RedisCommand.Option(existenceModifier.name, emptyList()))
        }

        if (get) {
            options.add(RedisCommand.Option("GET", emptyList()))
        }

        if (expiry != null) {
            options.add(expiry.serialize())
        }

        return RedisCommand("SET", setReader, listOf(key, value), options)
    }

    public fun touch(key: String, vararg keys: String): RedisCommand<Long> =
        RedisCommand("TOUCH", RedisTypeReader.Long, key, *keys)

    public fun ttl(key: String): RedisCommand<Long> =
        RedisCommand("TTL", RedisTypeReader.Long, key)

    public fun type(key: String): RedisCommand<String> =
        RedisCommand("TYPE", RedisTypeReader.String, key)

    public fun unlink(key: String, vararg keys: String): RedisCommand<Long> =
        RedisCommand("UNLINK", RedisTypeReader.Long, key, *keys)

    public fun wait(numreplicas: Long, timeout: Long): RedisCommand<Long> =
        RedisCommand("WAIT", RedisTypeReader.Long, numreplicas, timeout)
}
