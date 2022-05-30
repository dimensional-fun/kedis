package mixtape.oss.kedis.command.group

import mixtape.oss.kedis.command.*
import mixtape.oss.kedis.command.type.PauseMode
import mixtape.oss.kedis.command.type.RedisTypeReader
import mixtape.oss.kedis.command.type.TrackingInfo
import mixtape.oss.kedis.command.type.UnblockMode

public interface ConnectionManagementCommands {
    public fun auth(password: String): RedisCommand<String> =
        RedisCommand("AUTH", RedisTypeReader.SimpleString, password)

    public fun auth(username: String, password: String): RedisCommand<String> =
        RedisCommand("AUTH", RedisTypeReader.SimpleString, username, password)

    public fun clientCaching(value: Boolean): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "CACHING", if (value) "yes" else "no")

    public fun clientGetName(): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.BulkString, "GETNAME")

    public fun clientGetRedir(): RedisCommand<Long> =
        RedisCommand("CLIENT", RedisTypeReader.Long, "GETREDIR")

    public fun clientHelp(): RedisCommand<List<String?>> =
        RedisCommand("CLIENT", RedisTypeReader.StringList, "HELP")

    public fun clientId(): RedisCommand<Long> =
        RedisCommand("CLIENT", RedisTypeReader.Long, "ID")

    public fun clientInfo(): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.BulkString, "INFO")

    /*public fun clientKill(): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.BulkString, "KILL")*/

    /*public fun clientList(): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.BulkString, "LIST")*/

    public fun clientNoEvict(value: Boolean): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "NO-EVICT", if (value) "yes" else "no")

    public fun clientPause(timeout: Long): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "PAUSE", timeout)

    public fun clientPause(timeout: Long, mode: PauseMode): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "PAUSE", timeout, mode)

    public fun clientReply(mode: PauseMode): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "REPLY", mode)

    public fun clientSetName(name: String): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "SETNAME", name)

    /*public fun clientTracking(): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "TRACKING", name)*/

    public fun clientTrackingInfo(): RedisCommand<TrackingInfo> =
        RedisCommand("CLIENT", TrackingInfo, "TRACKINGINFO")

    public fun clientUnblock(clientId: Long): RedisCommand<Long> =
        RedisCommand("CLIENT", RedisTypeReader.Long, "UNBLOCK", clientId)

    public fun clientUnblock(clientId: Long, mode: UnblockMode): RedisCommand<Long> =
        RedisCommand("CLIENT", RedisTypeReader.Long, "UNBLOCK", clientId)

    public fun clientUnpause(): RedisCommand<String> =
        RedisCommand("CLIENT", RedisTypeReader.SimpleString, "UNPAUSE")

    public fun echo(message: Any): RedisCommand<String> =
        RedisCommand("ECHO", RedisTypeReader.BulkString, message)

    public fun hello(): RedisCommand<String> =
        RedisCommand("HELLO", RedisTypeReader.BulkString)

    public fun ping(): RedisCommand<String> =
        RedisCommand("PING", RedisTypeReader.SimpleString)

    public fun quit(): RedisCommand<String> =
        RedisCommand("QUIT", RedisTypeReader.SimpleString)

    public fun reset(): RedisCommand<String> =
        RedisCommand("RESET", RedisTypeReader.SimpleString)

    public fun select(index: Long): RedisCommand<String> =
        RedisCommand("SELECT", RedisTypeReader.SimpleString)
}
