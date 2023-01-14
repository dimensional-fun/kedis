import kedis.tools.escaped
import org.junit.jupiter.api.Test

class StringExtensionsTest {
    @Test
    fun `String#escaped escapes CR and LF`() {
        assert("Test\r\n".escaped == "Test\\r\\n")
        assert("Test\u200B".escaped == "Test\u200B")
    }
}
