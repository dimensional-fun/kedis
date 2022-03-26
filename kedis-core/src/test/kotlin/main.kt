import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.RedisCommands
import mixtape.oss.kedis.command.RedisTypeReader
import mixtape.oss.kedis.util.ping
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
suspend fun main() {
    val client = RedisClient("redis://:password@127.0.0.1")

    println(client.sendCommand(RedisCommand("KEYS", RedisTypeReader.StringList, "*")))

    val info = client.sendCommand(RedisCommands.info)!!
        .lines()
        .filterNot { it.startsWith("#") || it.isBlank() }
        .map { it.split(':') }
        .associate { it[0] to it[1] }

    println(info)

    println(measureTimedValue { client.ping() })
}
