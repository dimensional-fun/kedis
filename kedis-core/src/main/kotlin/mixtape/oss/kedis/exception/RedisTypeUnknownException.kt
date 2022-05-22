package mixtape.oss.kedis.exception

public class RedisTypeUnknownException(public val char: Char? = null) : RedisProtocolException("Unknown redis type was found: $char")
