package kedis.client.command.type

import kedis.protocol.Rawable

public enum class Where : Rawable {
    LEFT,
    RIGHT;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
