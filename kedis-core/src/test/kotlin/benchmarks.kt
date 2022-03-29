import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.command.RedisCommands
import mixtape.oss.kedis.pipelined
import redis.clients.jedis.*
import kotlin.system.measureTimeMillis

val iterations = 100
val redisUri = "redis://:password@127.0.0.1:6379"

suspend fun main() {
    val kedis = RedisClient(redisUri)
    measure("Kedis pipeline operation", iterations) {
        val pipeline = kedis.pipelined {
            +RedisCommands.del("a", "b")
            +RedisCommands.info()
            +RedisCommands.ping()
            +RedisCommands.exists("test")
        }

        pipeline.execute()
    }

    val jedis = Jedis(redisUri)

    measure("Jedis pipeline operation", iterations) {
        val pipeline = jedis.pipelined()
        pipeline.del("a", "b")
        pipeline.appendCommand(CommandObject(CommandArguments(Protocol.Command.INFO), BuilderFactory.STRING))
        pipeline.appendCommand(CommandObject(CommandArguments(Protocol.Command.PING), BuilderFactory.STRING))
        pipeline.exists("test")
        pipeline.syncAndReturnAll()
    }
}

inline fun measure(name: String, iterations: Int, block: () -> Unit) {
    val results = mutableListOf<Long>()
    repeat(iterations) {
        val timed = measureTimeMillis {
            block()
        }

        results.add(timed)
    }

    println(
        """
        --------------------------------------------------------------------
        $name, ${results.size} iterations
        Avg: ${results.average()}ms Min: ${results.minOrNull()}ms, Max: ${results.maxOrNull()}ms
        """.trimIndent())
}
