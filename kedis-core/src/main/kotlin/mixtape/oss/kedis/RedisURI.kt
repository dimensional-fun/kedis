package mixtape.oss.kedis

import io.ktor.util.network.*
import java.net.URI

public class RedisURI(uri: URI) {
    public constructor(uri: String) : this(URI(if (uri.startsWith("redis://")) uri else "redis://$uri"))

    public val auth: RedisAuth?

    public val address: NetworkAddress

    public val database: Int

    init {
        require(uri.scheme == "redis") { "uri scheme does not match" }

        /* get authentication */
        val userParts = uri.userInfo?.split(":".toRegex(), 2)
        auth = userParts?.let {
            val (username, password) = userParts
            RedisAuth(username.ifBlank { null }, password)
        }

        /* address */
        address = NetworkAddress(uri.host ?: "127.0.0.1", uri.port.takeUnless { it == -1 } ?: 6379)

        /* database */
        database = uri.path?.removePrefix("/")?.toIntOrNull() ?: 0
    }

    override fun toString(): String {
        return "redis://${auth?.asString?.let { "$it@" } ?: ""}${address.hostname}:${address.port}/$database"
    }
}
