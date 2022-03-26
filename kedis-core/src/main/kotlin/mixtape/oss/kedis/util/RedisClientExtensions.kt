package mixtape.oss.kedis.util

import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.RedisTypeReader

public suspend fun RedisClient.ping(): String? =
    sendCommand(RedisCommand("PING", RedisTypeReader.SimpleString))

public suspend fun RedisClient.del(keys: List<String>): Long? =
    sendCommand(RedisCommand("DEL", RedisTypeReader.Long, keys))

public suspend fun RedisClient.del(vararg keys: String): Long? =
    sendCommand(RedisCommand("DEL", RedisTypeReader.Long, *keys))
