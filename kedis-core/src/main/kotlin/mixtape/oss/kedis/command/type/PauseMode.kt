package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

public enum class PauseMode : Rawable {
    WRITE,
    ALL;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
