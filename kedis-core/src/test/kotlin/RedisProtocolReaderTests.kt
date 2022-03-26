import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import mixtape.oss.kedis.protocol.RedisProtocolReader
import mixtape.oss.kedis.protocol.RedisProtocolWriter
import org.junit.jupiter.api.Test

class RedisProtocolReaderTests {
    val testArr = listOf(null, "a", 1, true, null, listOf("b", 2, null, false))

    @Test
    fun `RedisProtocolReader reads arrays correctly`() = runBlocking {
        val a = RedisProtocolWriter.writeArray(testArr)
        val b = RedisProtocolWriter.writeArray(RedisProtocolReader.readArray(ByteReadChannel(a)))

        assert(b.contentEquals(a))
    }

    @Test
    fun `RedisProtocolReader reads arrays correctly and bulk strings`() = runBlocking {
        val a = RedisProtocolWriter.writeArray(testArr, true)
        val b = RedisProtocolWriter.writeArray(RedisProtocolReader.readArray(ByteReadChannel(a)), true)

        assert(b.contentEquals(a))
    }
}
