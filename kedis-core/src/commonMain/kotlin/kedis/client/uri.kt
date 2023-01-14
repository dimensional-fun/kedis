// https://www.iana.org/assignments/uri-schemes/prov/redis

package kedis.client

import io.ktor.http.*
import io.ktor.network.sockets.*
import kedis.tools.REDIS
import kedis.tools.REDISS

public fun RedisURI(uri: String): RedisURI = RedisURI(Url(uri))

public fun RedisURI(url: Url): RedisURI {
    val normalized = url.normalize()

    val db = normalized.pathSegments.drop(1)
        .firstOrNull()
        ?.toIntOrNull()
        ?: normalized.parameters["db"]?.toIntOrNull()
        ?: 0

    return RedisURI(
        db,
        normalized.protocol.name === URLProtocol.REDISS.name,
        InetSocketAddress(normalized.host, normalized.port),
        normalized.password
            ?.takeUnless { it.isBlank() }
            ?.let { RedisAuth(normalized.user ?: "default", it) }

    )
}

private fun Url.normalize(): Url {
    val normalized = when (protocol.name.lowercase()) {
        "redis" -> URLProtocol.REDIS
        "rediss" -> URLProtocol.REDISS
        else -> error("Unsupported Protocol: $protocol")
    }

    val builder = URLBuilder(this)
    builder.protocol = normalized

    return builder.build()
}
