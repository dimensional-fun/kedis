@file:OptIn(ExperimentalContracts::class)

package kedis.tools

import kedis.protocol.RedisType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public val String.escaped: String
    get() = replace("\r", "\\r").replace("\n", "\\n")

internal fun String.containsLF(): Boolean = any { it == '\n' }

internal fun String.containsCR(): Boolean = any { it == '\r' }

internal fun String.removePrefix(prefix: Char): String = if (startsWith(prefix)) drop(1) else this

internal fun String.removePrefix(type: RedisType): String = removePrefix(type.char)

internal inline fun <T, R> T.doIf(value: Boolean, block: T.() -> R): T where R : T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (value) return block(this)
    return this
}
