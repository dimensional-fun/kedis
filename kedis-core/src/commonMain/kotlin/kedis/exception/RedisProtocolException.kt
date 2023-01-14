package kedis.exception

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

public open class RedisProtocolException public constructor(message: String?) : RuntimeException(message)

@OptIn(ExperimentalContracts::class)
public inline fun require(value: Boolean, lazyMessage: () -> Any) {
    contract {
        returns() implies value
    }

    if (!value) {
        val message = lazyMessage()
        throw RedisProtocolException(message.toString())
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T : Any> notNullable(value: T?, lazyMessage: () -> Any): T {
    contract {
        returns() implies (value != null)
    }

    if (value == null) {
        val message = lazyMessage()
        throw RedisProtocolException(message.toString())
    } else {
        return value
    }
}
