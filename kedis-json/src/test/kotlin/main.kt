import mixtape.oss.kedis.RedisClient
import mixtape.oss.kedis.json.RedisJson
import mixtape.oss.kedis.json.get
import mixtape.oss.kedis.json.set

suspend fun main() {
    val client = RedisClient("redis://:password@localhost")
    val json = RedisJson(client)

    json.set("test", Test(listOf("so cool lmfaoindwoandaui"), 4, Test(listOf("1", "2", "3", "4", "5"), 69)))

    println(json.arrLen("test", "$..a"))
    println(json.get<Test>("test"))
}

@kotlinx.serialization.Serializable
data class Test(val a: List<String>, val b: Int, val c: Test? = null)
