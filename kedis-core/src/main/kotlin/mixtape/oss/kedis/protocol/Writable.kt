package mixtape.oss.kedis.protocol

import io.ktor.utils.io.core.*

public interface Writable {
    public fun write(writer: RedisProtocolWriter): ByteArray

    public suspend fun writePacket(writer: RedisProtocolWriter): ByteReadPacket = ByteReadPacket(write(writer))
}
