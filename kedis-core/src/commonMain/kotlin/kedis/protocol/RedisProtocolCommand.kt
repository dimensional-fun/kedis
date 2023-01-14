package kedis.protocol

public interface RedisProtocolCommand {
    public val literal: String

    public data class Literal(override val literal: String) : RedisProtocolCommand
}
