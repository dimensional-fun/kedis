
import mixtape.oss.kedis.protocol.resp2.Resp2ProtocolWriter
import org.junit.jupiter.api.Test

class RedisProtocolWriterTests {
    @Test
    fun `Protocol encodes correctly`() {
        assert(compose(null) == "$-1\r\n")

        assert(compose(listOf("hi", "hello", "new\nline")) == "*3\r\n+hi\r\n+hello\r\n$8\r\nnew\nline\r\n")

        assert(compose(listOf(1, 2, 42069, -5, 4902)) == "*5\r\n:1\r\n:2\r\n:42069\r\n:-5\r\n:4902\r\n")

        assert(compose(false) == ":0\r\n")

        assert(compose(true) == ":1\r\n")
    }

    fun compose(value: Any?): String {
        return Resp2ProtocolWriter.write(value).decodeToString()
    }
}
