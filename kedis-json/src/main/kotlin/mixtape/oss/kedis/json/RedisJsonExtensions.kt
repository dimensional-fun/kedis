package mixtape.oss.kedis.json

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/* JSON.SET */
public suspend inline fun <reified T : Any> RedisJson.set(key: String, value: T, path: String = RedisJson.ROOT_PATH): String? =
    set(key, value.toJson(this), path)

public suspend inline fun <reified T : Any> RedisJson.setxx(key: String, value: T, path: String = RedisJson.ROOT_PATH): String? =
    setxx(key, value.toJson(this), path)

public suspend inline fun <reified T : Any> RedisJson.setnx(key: String, value: T, path: String = RedisJson.ROOT_PATH): String? =
    setnx(key, value.toJson(this), path)

/* JSON.GET */
public suspend inline fun <reified T : Any> RedisJson.get(key: String, vararg paths: String): T? =
    get(key, *paths)?.decodeJson(this)

public suspend inline fun <reified T : Any> RedisJson.get(key: String): T? =
    get(key, RedisJson.ROOT_PATH)?.decodeJson(this)

public inline fun <reified T> T.toJson(redisJson: RedisJson): String =
    redisJson.serializer.encodeToString(this)

public inline fun <reified T> String.decodeJson(redisJson: RedisJson): T =
    redisJson.serializer.decodeFromString(this)
