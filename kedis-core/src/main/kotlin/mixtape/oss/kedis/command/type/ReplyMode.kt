package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

public enum class ReplyMode : Rawable {
    On,
    Off,
    Skip;

    override fun bytes(): ByteArray = name.uppercase().encodeToByteArray()
}
