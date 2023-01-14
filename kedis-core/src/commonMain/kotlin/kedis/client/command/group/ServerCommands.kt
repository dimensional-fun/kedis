package kedis.client.command.group

import kedis.client.command.RedisCommand
import kedis.client.command.type.RedisTypeReader

public interface ServerCommands {
    public fun info(): RedisCommand<String> =
        RedisCommand("INFO", RedisTypeReader.BulkString)

    public fun <T> acl(command: String, typeReader: RedisTypeReader<T>, vararg args: Any?): RedisCommand<T> =
        RedisCommand("ACL", typeReader, command, *args)

    public fun aclCat(): RedisCommand<List<String?>> =
        acl("CAT", RedisTypeReader.StringList)

    public fun aclDelUser(vararg usernames: String): RedisCommand<Long> =
        acl("DELUSER", RedisTypeReader.Long, *usernames)

    public fun aclDryRun(username: String, command: RedisCommand<*>): RedisCommand<Long> =
        acl("DRYRUN", RedisTypeReader.Long, username, command)

    public fun aclGenPass(bits: Int = 256): RedisCommand<String> =
        acl("GENPASS", RedisTypeReader.BulkString, bits)

    public fun aclGetUser(username: String): RedisCommand<*> =
        TODO("Figure out a way of actually handling the response for this.")

    public fun aclHelp(): RedisCommand<List<String?>> =
        acl("HELP", RedisTypeReader.StringList)

    public fun aclList(): RedisCommand<List<String?>> =
        acl("LIST", RedisTypeReader.StringList)

    public fun aclLoad(): RedisCommand<String> =
        acl("LOAD", RedisTypeReader.SimpleString)
}
