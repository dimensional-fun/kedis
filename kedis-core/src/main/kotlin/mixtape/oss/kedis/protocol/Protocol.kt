package mixtape.oss.kedis.protocol

import mixtape.oss.kedis.protocol.resp2.Resp2ProtocolReader
import mixtape.oss.kedis.protocol.resp2.Resp2ProtocolWriter

public open class Protocol(
    public val id: Int,
    public val name: String,
    public val reader: RedisProtocolReader,
    public val writer: RedisProtocolWriter
) : Rawable {
    override fun bytes(): ByteArray = name.encodeToByteArray()

    override fun toString(): String {
        return "RedisProtocol(id=$id, name=$name)"
    }

    public object RESP2 : Protocol(2, "RESP2", Resp2ProtocolReader, Resp2ProtocolWriter)
}
