package kedis.client.command.type

import kedis.protocol.Rawable

public enum class UnblockMode : Rawable {
    TIMEOUT,
    ERROR;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
