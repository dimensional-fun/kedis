package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

public enum class Position : Rawable {
    BEFORE,
    AFTER;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
