package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

public enum class BitOperation : Rawable {
    AND,
    OR,
    XOR,
    NOT;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
