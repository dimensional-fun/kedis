package mixtape.oss.kedis.command.group

import mixtape.oss.kedis.command.type.Position
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.type.RedisTypeReader
import mixtape.oss.kedis.command.type.Where
import mixtape.oss.kedis.util.doIf

public interface ListCommands {
    public fun blmove(source: String, destination: String, whereFrom: Where, whereTo: Where): RedisCommand<String> =
        RedisCommand("BLMOVE", RedisTypeReader.BulkString, source, destination, whereFrom, whereTo)

    public fun blmpop(
        timeout: Long,
        numkeys: Long,
        key: String,
        vararg keys: String,
        where: Where,
        count: Long? = null,
    ): RedisCommand<List<String?>> =
        RedisCommand("BLMPOP", RedisTypeReader.StringList, timeout, numkeys, key, *keys, where)
            .doIf(count != null) { withOption("COUNT", count!!) }

    public fun blpop(key: String, vararg keys: String, timeout: Long): RedisCommand<List<String?>> =
        RedisCommand("BLPOP", RedisTypeReader.StringList, key, *keys, timeout)

    public fun brpop(key: String, vararg keys: String, timeout: Long): RedisCommand<List<String?>> =
        RedisCommand("BRPOP", RedisTypeReader.StringList, key, *keys, timeout)

    public fun brpoplpush(source: String, destination: String, timeout: Long): RedisCommand<String> =
        RedisCommand("BRPOPLPUSH", RedisTypeReader.BulkString, source, destination, timeout)

    public fun lindex(key: String, index: Long): RedisCommand<String> =
        RedisCommand("LINDEX", RedisTypeReader.BulkString, key, index)

    public fun linsert(key: String, position: Position, pivot: String, element: String): RedisCommand<Long> =
        RedisCommand("LINSERT", RedisTypeReader.Long, key, position, pivot, element)

    public fun llen(key: String): RedisCommand<Long> =
        RedisCommand("LLEN", RedisTypeReader.Long, key)

    public fun lmove(source: String, destination: String, whereFrom: Where, whereTo: Where): RedisCommand<String> =
        RedisCommand("LMOVE", RedisTypeReader.BulkString, source, destination, whereFrom, whereTo)

    public fun lmpop(
        numkeys: Long,
        key: String,
        vararg keys: String,
        where: Where,
        count: Long? = null,
    ): RedisCommand<List<String?>> =
        RedisCommand("LMPOP", RedisTypeReader.StringList, numkeys, key, *keys, where)
            .doIf(count != null) { withOption("COUNT", count!!) }

    public fun lpop(key: String, count: Long? = null): RedisCommand<List<String?>> =
        RedisCommand("LPOP", RedisTypeReader.StringList, key)
            .doIf(count != null) { withArg(count!!) }

    public fun lpos(
        key: String,
        element: Any,
        rank: Long? = null,
        count: Long? = null,
        maxLen: Long? = null,
    ): RedisCommand<List<String?>> =
        RedisCommand("LPOS", RedisTypeReader.StringList, key, element)
            .doIf(rank != null) { withOption("RANK", rank!!) }
            .doIf(count != null) { withOption("COUNT", count!!) }
            .doIf(maxLen != null) { withOption("MAXLEN", maxLen!!) }

    public fun lpush(key: String, element: Any, vararg elements: Any): RedisCommand<Long> =
        RedisCommand("LPUSH", RedisTypeReader.Long, key, element, *elements)
}
