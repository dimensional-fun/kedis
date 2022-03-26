package mixtape.oss.kedis.command

import io.ktor.utils.io.core.*
import mixtape.oss.kedis.protocol.RedisProtocolCommand
import mixtape.oss.kedis.protocol.RedisProtocolWriter
import mixtape.oss.kedis.util.CRLF

public data class RedisCommand<T>(
    val name: String,
    val reader: RedisTypeReader<T>,
    val args: List<Any?> = emptyList(),
) {
    public constructor(literal: String, reader: RedisTypeReader<T>, vararg args: Any?) : this(
        literal,
        reader,
        args.toList()
    )

    public constructor(command: RedisProtocolCommand, reader: RedisTypeReader<T>, vararg args: Any?) : this(
        command.literal,
        reader,
        args.toList()
    )

    private val completeArgs: List<Any?>
        get() = listOf(name, *args.toTypedArray())

    public fun bytes(): ByteArray {
        return if (args.isEmpty()) name.encodeToByteArray() + CRLF else RedisProtocolWriter.writeArray(completeArgs, true)
    }

    public fun packet(): ByteReadPacket {
        return ByteReadPacket(bytes())
    }
}
