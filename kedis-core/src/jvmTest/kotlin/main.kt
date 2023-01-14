import kedis.client.RedisClient
import kedis.client.command.RedisCommand
import kedis.client.command.type.RedisTypeReader
import kedis.protocol.Protocol

suspend fun main() {
    val kedis = RedisClient("redis://localhost", Protocol.RESP2)

//    val pipeline = kedis.pipelined {
//        val aclHelp = +RedisCommands.aclHelp()
//        aclHelp.response.invokeOnCompletion {
//            println(aclHelp.response.getCompleted()?.withIndex()?.joinToString("\n") { (i, v) -> "${i + 1}) $v" })
//        }
//
//        val latency = +RedisCommand("LATENCY", RedisTypeReader.String, "DOCTOR")
//        latency.response.invokeOnCompletion {
//            println(latency.response.getCompleted())
//        }
//
//        val mget = +RedisCommand("MGET", RedisTypeReader.UnknownList, "c", "d", "e", "koin")
//        mget.response.invokeOnCompletion {
//            println(mget.response.getCompleted())
//        }
//    }

    println(kedis.executeCommand(RedisCommand("LATENCY", RedisTypeReader.String, "DOCTOR")))
}
