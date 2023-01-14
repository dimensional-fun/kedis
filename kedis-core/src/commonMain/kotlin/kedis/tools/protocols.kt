package kedis.tools

import io.ktor.http.*

/** Insecure Redis */
public val URLProtocol.Companion.REDIS: URLProtocol
    get() = URLProtocol("redis", 6379)

/** Secure Redis */
public val URLProtocol.Companion.REDISS: URLProtocol
    get() = URLProtocol("rediss", 6379)