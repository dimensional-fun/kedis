package mixtape.oss.kedis.command.group

import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.command.type.RedisTypeReader

public interface PubSubCommands {
    public fun publish(channel: String, message: String): RedisCommand<Long> =
        RedisCommand("PUBLISH", RedisTypeReader.Long, channel, message)

    public fun publish(channel: String, message: ByteArray): RedisCommand<Long> =
        RedisCommand("PUBLISH", RedisTypeReader.Long, channel, String(message))

    public fun spublish(channel: String, message: String): RedisCommand<Long> =
        RedisCommand("SPUBLISH", RedisTypeReader.Long, channel, message)

    public fun spublish(channel: String, message: ByteArray): RedisCommand<Long> =
        RedisCommand("SPUBLISH", RedisTypeReader.Long, channel, String(message))
}
