package kedis.client.command.group

import kedis.client.command.RedisCommand
import kedis.client.command.type.BitIndex
import kedis.client.command.type.BitOperation
import kedis.client.command.type.RedisTypeReader

public interface BitmapCommands {
    public fun bitcount(key: String): RedisCommand<Long> =
        RedisCommand("BITCOUNT", RedisTypeReader.Long, key)

    public fun bitcount(key: String, start: Long, end: Long): RedisCommand<Long> =
        RedisCommand("BITCOUNT", RedisTypeReader.Long, key, start, end)

    public fun bitcount(key: String, start: Long, end: Long, index: BitIndex): RedisCommand<Long> =
        RedisCommand("BITCOUNT", RedisTypeReader.Long, key, start, end, index)

    /*public fun bitfield(): RedisCommand<> =
        RedisCommand("BITFIELD", RedisTypeReader.Array)*/

    /*public fun bitfieldRO(): RedisCommand<> =
        RedisCommand("BITFIELD_RO", RedisTypeReader.Array)*/

    public fun bitop(operation: BitOperation, destkey: String, key: String, vararg keys: String): RedisCommand<Long> =
        RedisCommand("BITOP", RedisTypeReader.Long, operation, destkey, key, *keys)

    public fun bitpos(key: String, bit: Long): RedisCommand<Long> =
        RedisCommand("BITPOS", RedisTypeReader.Long, key, bit)

    public fun bitpos(key: String, bit: Long, start: Long): RedisCommand<Long> =
        RedisCommand("BITPOS", RedisTypeReader.Long, key, bit, start)

    public fun bitpos(key: String, bit: Long, start: Long, end: Int): RedisCommand<Long> =
        RedisCommand("BITPOS", RedisTypeReader.Long, key, bit, start, end)

    public fun bitpos(key: String, bit: Long, start: Long, end: Int, index: BitIndex): RedisCommand<Long> =
        RedisCommand("BITPOS", RedisTypeReader.Long, key, bit, start, end, index)

    public fun getbit(key: String, offset: Long): RedisCommand<Long> =
        RedisCommand("GETBIT", RedisTypeReader.Long, key, offset)

    public fun getbit(key: String, offset: Long, value: Long): RedisCommand<Long> =
        RedisCommand("SETBIT", RedisTypeReader.Long, key, offset, value)
}
