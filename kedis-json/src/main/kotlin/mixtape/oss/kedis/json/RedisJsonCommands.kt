package mixtape.oss.kedis.json

import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.RedisTypeReader

public object RedisJsonCommands {
    public fun set(key: String, path: String, value: String): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.SET, RedisTypeReader.String, key, path, value)

    public fun setnx(key: String, path: String, value: String): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.SET, RedisTypeReader.String, key, path, value, "NX")

    public fun setxx(key: String, path: String, value: String): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.SET, RedisTypeReader.String, key, path, value, "XX")

    public fun get(key: String, vararg paths: String): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.GET, RedisTypeReader.String, key, *paths)

    public fun mget(key: String, vararg paths: String): RedisCommand<List<String>> =
        RedisCommand(RedisJsonCommand.MGET, RedisTypeReader.StringList, key, *paths)

    public fun del(key: String, path: String): RedisCommand<Long> =
        RedisCommand(RedisJsonCommand.DEL, RedisTypeReader.Long, key, path)

    public fun clear(key: String, path: String): RedisCommand<Long> =
        RedisCommand(RedisJsonCommand.CLEAR, RedisTypeReader.Long, key, path)

    public fun incrBy(key: String, path: String, by: Int): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.NUM_INCR_BY, RedisTypeReader.String, key, path, by)

    public fun multBy(key: String, path: String, by: Int): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.NUM_MULT_BY, RedisTypeReader.String, key, path, by)

    public fun toggle(key: String, path: String): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.TOGGLE, RedisTypeReader.String, key, path)

    public fun strAppend(key: String, value: String, path: String? = null): RedisCommand<List<Long>> =
        RedisCommand(RedisJsonCommand.STR_APPEND, RedisTypeReader.LongList, key, path, value)

    public fun strLen(key: String, path: String? = null): RedisCommand<List<Long>> =
        RedisCommand(RedisJsonCommand.STR_LEN, RedisTypeReader.LongList, key, path)

    public fun arrAppend(key: String, path: String, vararg values: String): RedisCommand<List<Long>> =
        RedisCommand(RedisJsonCommand.ARR_APPEND, RedisTypeReader.LongList, key, path, *values)

    public fun arrIndex(key: String, path: String, value: String, start: Int? = null, stop: Int? = null): RedisCommand<List<Long>> =
        RedisCommand(RedisJsonCommand.ARR_INDEX, RedisTypeReader.LongList, key, path, value, start, stop)

    public fun arrInsert(key: String, path: String, index: Int, vararg values: String): RedisCommand<List<Long>> =
        RedisCommand(RedisJsonCommand.ARR_INSERT, RedisTypeReader.LongList, key, path, index, *values)

    public fun arrLen(key: String, path: String? = null): RedisCommand<List<Long>> =
        RedisCommand(RedisJsonCommand.ARR_LEN, RedisTypeReader.LongList, key, path)

    public fun arrPop(key: String, path: String? = null, index: Int? = null): RedisCommand<List<String>> =
        RedisCommand(RedisJsonCommand.ARR_POP, RedisTypeReader.StringList, key, path, index)

    public fun arrTrim(key: String, path: String, start: Int, stop: Int): RedisCommand<List<Long>> =
        RedisCommand(RedisJsonCommand.ARR_TRIM, RedisTypeReader.LongList, key, path, start, stop)

    public fun type(key: String, path: String? = null): RedisCommand<String> =
        RedisCommand(RedisJsonCommand.TYPE, RedisTypeReader.String, key, path)
}
