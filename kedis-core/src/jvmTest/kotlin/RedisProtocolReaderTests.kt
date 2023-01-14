import io.ktor.utils.io.*
import kedis.protocol.resp2.Resp2ProtocolReader
import kedis.protocol.resp2.Resp2ProtocolWriter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class RedisProtocolReaderTests {
    private val writer = Resp2ProtocolWriter
    private val reader = Resp2ProtocolReader
    private val testArr = listOf(null, "a", 1, true, null, listOf("b", 2, null, false))

    @Test
    fun `RedisProtocolReader reads arrays correctly`() = runBlocking {
        val a = writer.writeArray(testArr)
        val b = reader.readArray(ByteReadChannel(a)).write(writer)

        assert(b.contentEquals(a))
    }

    @Test
    fun `RedisProtocolReader reads arrays correctly and bulk strings`() = runBlocking {
        val a = writer.writeArray(testArr, true)
        val b = reader.readArray(ByteReadChannel(a)).write(writer, true)

        assert(b.contentEquals(a))
    }
}
