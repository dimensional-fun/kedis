package mixtape.oss.kedis.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal inline fun <reified T> Any.into(): T = this as T

internal inline fun <T, R> T.doIf(value: Boolean, block: T.() -> R): T where R : T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (value) return block(this)
    return this
}
