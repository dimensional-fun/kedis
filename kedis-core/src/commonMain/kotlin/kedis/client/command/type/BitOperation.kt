package kedis.client.command.type

import kedis.protocol.Rawable

public enum class BitOperation : Rawable {
    AND,
    OR,
    XOR,
    NOT;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
