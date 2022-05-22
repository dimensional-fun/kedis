@file:OptIn(ExperimentalCoroutinesApi::class)

import kotlinx.coroutines.ExperimentalCoroutinesApi
import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.group.RedisCommands
import mixtape.oss.kedis.command.type.RedisTypeReader
import mixtape.oss.kedis.pipelined
import mixtape.oss.kedis.protocol.Protocol

suspend fun main() {
    val kedis = RedisClient("redis://:password@localhost", Protocol.RESP2)

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
