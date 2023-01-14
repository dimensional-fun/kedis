package kedis.client

import io.ktor.network.sockets.*

public data class RedisURI(
    val db: Int,
    val secure: Boolean,
    val address: InetSocketAddress,
    val auth: RedisAuth?
)