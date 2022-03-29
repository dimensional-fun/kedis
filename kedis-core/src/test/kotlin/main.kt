import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.command.RedisCommands

suspend fun main() {
    val kedis = RedisClient("redis://:password@localhost")

    println(kedis.executeCommand(RedisCommands.aclHelp())?.withIndex()?.joinToString("\n") { (i, v) -> "${i + 1}) $v" })
}
