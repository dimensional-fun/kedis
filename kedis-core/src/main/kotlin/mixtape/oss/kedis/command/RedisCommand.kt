package mixtape.oss.kedis.command

import mixtape.oss.kedis.command.type.RedisTypeReader
import mixtape.oss.kedis.protocol.Writable
import mixtape.oss.kedis.protocol.RedisProtocolCommand
import mixtape.oss.kedis.protocol.RedisProtocolWriter
import mixtape.oss.kedis.util.CRLF

public data class RedisCommand<T>(
    val name: String,
    val reader: RedisTypeReader<T>,
    val args: List<Any?> = emptyList(),
    val options: List<Option> = emptyList(),
) : Writable {
    public constructor(
        literal: String,
        reader: RedisTypeReader<T>,
        vararg args: Any?,
        options: List<Option> = emptyList(),
    ) : this(
        literal,
        reader,
        args.toList(),
        options
    )

    public constructor(
        command: RedisProtocolCommand,
        reader: RedisTypeReader<T>,
        vararg args: Any?,
        options: List<Option> = emptyList()
    ) : this(
        command.literal,
        reader,
        args.toList(),
        options
    )

    private val completeArgs: List<Any?>
        get() = listOf(name) + args + options.flatMap { it.completeArgs }

    public fun withOption(name: String, vararg args: Any): RedisCommand<T> {
        return RedisCommand(this.name, this.reader, this.args, this.options + Option(name, args.toList()))
    }

    public fun withArg(value: Any): RedisCommand<T> {
        return RedisCommand(this.name, this.reader, this.args + value, this.options)
    }

    public override fun write(writer: RedisProtocolWriter): ByteArray {
        return if (args.isEmpty()) {
            name.encodeToByteArray() + CRLF
        } else {
            writer.writeArray(completeArgs, true)
        }
    }

    public data class Option(val name: String, val args: List<Any>) {
        val completeArgs: List<Any>
            get() = listOf(name) + args
    }
}
