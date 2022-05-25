package mixtape.oss.kedis.util

import mixtape.oss.kedis.RedisAuth
import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.group.RedisCommands
import mixtape.oss.kedis.command.type.ExistenceModifier
import mixtape.oss.kedis.command.type.KeyExpiry
import mixtape.oss.kedis.command.type.RedisTypeReader

public suspend fun RedisClient.ping(): String? =
    executeCommand(RedisCommands.ping())

public suspend fun RedisClient.get(key: String): String? =
    executeCommand(RedisCommand("GET", RedisTypeReader.String, key))

public suspend fun RedisClient.set(
    key: String,
    value: String,
    existenceModifier: ExistenceModifier? = null,
    get: Boolean = false,
    expiry: KeyExpiry? = null
): Any? = executeCommand(RedisCommands.set(key, value, existenceModifier, get, expiry))

public suspend fun RedisClient.del(keys: List<String>): Long? =
    executeCommand(RedisCommand("DEL", RedisTypeReader.Long, keys))

public suspend fun RedisClient.del(vararg keys: String): Long? =
    executeCommand(RedisCommand("DEL", RedisTypeReader.Long, *keys))

public suspend fun RedisClient.quit(): String? =
    executeCommand(RedisCommands.quit())

public suspend fun RedisClient.auth(auth: RedisAuth): String? =
    if (auth.username.isNullOrBlank()) auth(auth.password) else auth(auth.username, auth.password)

public suspend fun RedisClient.auth(password: String): String? =
    executeCommand(RedisCommands.auth(password))

public suspend fun RedisClient.auth(username: String, password: String): String? =
    executeCommand(RedisCommands.auth(username, password))
