package mixtape.oss.kedis.json

import kotlinx.serialization.json.Json
import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.RedisPool
import mixtape.oss.kedis.command.RedisCommand

public class RedisJson(public val serializer: Json = DEFAULT_SERIALIZER) {
    public companion object {
        public const val ROOT_PATH: String = "."

        public val DEFAULT_SERIALIZER: Json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        public fun isOK(string: String?): Boolean =
            string == "OK"

        public fun bulkReplyToIntegerList(values: String): List<Int?> {
            return values
                .removeSurrounding("[", "]")
                .split(',')
                .map { if (it == "null") null else it.toInt() }
        }

        public fun argsOf(vararg strings: String): Array<ByteArray> =
            strings.map { it.encodeToByteArray() }.toTypedArray()
    }

    private var pool: RedisPool? = null
    private var connection: RedisClient? = null

    public constructor(pool: RedisPool, serializer: Json = DEFAULT_SERIALIZER) : this(serializer) {
        this.pool = pool
    }

    public constructor(connection: RedisClient, serializer: Json = DEFAULT_SERIALIZER) : this(serializer) {
        this.connection = connection
    }

    @JvmOverloads
    public suspend fun set(key: String, value: String, path: String = "."): String? =
        executeCommand(RedisJsonCommands.set(key, path, value))

    @JvmOverloads
    public suspend fun setxx(key: String, value: String, path: String = "$"): String? =
        executeCommand(RedisJsonCommands.setxx(key, path, value))

    @JvmOverloads
    public suspend fun setnx(key: String, value: String, path: String = "$"): String? =
        executeCommand(RedisJsonCommands.setnx(key, path, value))

    public suspend fun get(key: String, vararg paths: String): String? =
        executeCommand(RedisJsonCommands.get(key, *paths))

    public suspend fun mget(path: String, vararg keys: String): List<String>? =
        executeCommand(RedisJsonCommands.mget(path, *keys))

    @JvmOverloads
    public suspend fun del(key: String, path: String = ROOT_PATH): Long? =
        executeCommand(RedisJsonCommands.del(key, path))

    @JvmOverloads
    public suspend fun clear(key: String, path: String = ROOT_PATH): Long? =
        executeCommand(RedisJsonCommands.clear(key, path))

    @JvmOverloads
    public suspend fun incrBy(key: String, by: Int = 1, path: String = ROOT_PATH): String? =
        executeCommand(RedisJsonCommands.incrBy(key, path, by))

    @JvmOverloads
    public suspend fun multBy(key: String, by: Int, path: String = ROOT_PATH): String? =
        executeCommand(RedisJsonCommands.multBy(key, path, by))

    @JvmOverloads
    public suspend fun toggle(key: String, path: String = ROOT_PATH): String? =
        executeCommand(RedisJsonCommands.toggle(key, path))

    @JvmOverloads
    public suspend fun strAppend(key: String, value: String, path: String = ROOT_PATH): List<Long>? =
        executeCommand(RedisJsonCommands.strAppend(key, value, path))

    @JvmOverloads
    public suspend fun strLen(key: String, path: String = ROOT_PATH): List<Long>? =
        executeCommand(RedisJsonCommands.strLen(key, path))

    @JvmOverloads
    public suspend fun type(key: String, path: String = ROOT_PATH): JsonType? =
        executeCommand(RedisJsonCommands.type(key, path))?.let { JsonType.find(it) ?: error(it) }

    @JvmOverloads
    public suspend fun arrLen(key: String, path: String = ROOT_PATH): List<Long>? =
        executeCommand(RedisJsonCommands.arrLen(key, path))

    @JvmOverloads
    public suspend fun arrPop(key: String, path: String = ROOT_PATH, index: Int? = null): List<String>? =
        executeCommand(RedisJsonCommands.arrPop(key, path, index))

    private suspend fun <T> executeCommand(command: RedisCommand<T>): T? = useConnection {
        return it.sendCommand(command)
    }

    private suspend inline fun <T> useConnection(block: (connection: RedisClient) -> T): T =
        pool?.use(block) ?: connection?.let(block) ?: error("A 'RedisPool' or 'RedisClient' must be given")
}
