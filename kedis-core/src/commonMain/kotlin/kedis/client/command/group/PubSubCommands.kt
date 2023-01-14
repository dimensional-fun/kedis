package kedis.client.command.group

import kedis.client.command.RedisCommand
import kedis.client.command.type.RedisTypeReader

public interface PubSubCommands {
    public fun publish(channel: String, message: String): RedisCommand<Long> =
        RedisCommand("PUBLISH", RedisTypeReader.Long, channel, message)

    public fun publish(channel: String, message: ByteArray): RedisCommand<Long> =
        RedisCommand("PUBLISH", RedisTypeReader.Long, channel, message.decodeToString())

    public fun spublish(channel: String, message: String): RedisCommand<Long> =
        RedisCommand("SPUBLISH", RedisTypeReader.Long, channel, message)

    public fun spublish(channel: String, message: ByteArray): RedisCommand<Long> =
        RedisCommand("SPUBLISH", RedisTypeReader.Long, channel, message.decodeToString())
}
