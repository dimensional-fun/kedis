package kedis.protocol

import io.ktor.utils.io.core.*

public interface Rawable {
    public fun bytes(): ByteArray

    public fun packet(): ByteReadPacket {
        return ByteReadPacket(bytes())
    }
}
