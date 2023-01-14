package kedis.client.command.type

import kedis.protocol.Rawable

public enum class ReplyMode : Rawable {
    On,
    Off,
    Skip;

    override fun bytes(): ByteArray = name.uppercase().encodeToByteArray()
}
