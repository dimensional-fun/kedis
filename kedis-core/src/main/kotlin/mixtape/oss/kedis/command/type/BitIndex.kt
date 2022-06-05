package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

public enum class BitIndex : Rawable {
    BYTE, BIT;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
