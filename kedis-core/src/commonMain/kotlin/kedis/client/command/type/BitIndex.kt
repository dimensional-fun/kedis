package kedis.client.command.type

import kedis.protocol.Rawable

public enum class BitIndex : Rawable {
    BYTE, BIT;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
