package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

public enum class UnblockMode : Rawable {
    TIMEOUT,
    ERROR;

    override fun bytes(): ByteArray = name.encodeToByteArray()
}
