package kedis.client.command.type

import kedis.protocol.Rawable

public enum class PauseMode : Rawable {
    WRITE,
    ALL;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
