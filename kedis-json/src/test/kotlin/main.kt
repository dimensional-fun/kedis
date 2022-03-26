import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.json.RedisJson
import mixtape.oss.kedis.json.set
import mixtape.oss.kedis.json.get

suspend fun main() {
    val client = RedisClient("redis://:password@localhost")
    val json = RedisJson(client)

    json.set("test", Test("so cool lmfaoindwoandaui", 4))

    println(json.get<Test>("test"))
}

@kotlinx.serialization.Serializable
data class Test(val a: String, val b: Int)
