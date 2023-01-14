package kedis.client.command.type

import kedis.protocol.Rawable

public enum class Position : Rawable {
    BEFORE,
    AFTER;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
