package mixtape.oss.kedis.util

import mixtape.oss.kedis.RedisAuth
import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.RedisTypeReader

public suspend fun RedisClient.ping(): String? =
    executeCommand(RedisCommand("PING", RedisTypeReader.SimpleString))

public suspend fun RedisClient.get(key: String): String? =
    executeCommand(RedisCommand("GET", RedisTypeReader.String, key))

public suspend fun RedisClient.del(keys: List<String>): Long? =
    executeCommand(RedisCommand("DEL", RedisTypeReader.Long, keys))

public suspend fun RedisClient.del(vararg keys: String): Long? =
    executeCommand(RedisCommand("DEL", RedisTypeReader.Long, *keys))

public suspend fun RedisClient.quit(): String? =
    executeCommand(RedisCommand("QUIT", RedisTypeReader.SimpleString))

public suspend fun RedisClient.auth(auth: RedisAuth): String? =
    if (auth.username.isNullOrBlank()) auth(auth.password) else auth(auth.username, auth.password)

public suspend fun RedisClient.auth(password: String): String? =
    executeCommand(RedisCommand("AUTH", RedisTypeReader.SimpleString, password))

public suspend fun RedisClient.auth(username: String, password: String): String? =
    executeCommand(RedisCommand("AUTH", RedisTypeReader.SimpleString, username, password))
