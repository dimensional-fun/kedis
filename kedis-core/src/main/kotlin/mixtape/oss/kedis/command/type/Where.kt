package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

public enum class Where : Rawable {
    LEFT,
    RIGHT;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
