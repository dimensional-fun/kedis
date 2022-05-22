package mixtape.oss.kedis.util

import mixtape.oss.kedis.protocol.RedisType

internal val String.escaped: String
    get() = replace("\r", "\\r").replace("\n", "\\n")

internal fun String.containsLF(): Boolean = any { it == '\n' }

internal fun String.containsCR(): Boolean = any { it == '\r' }

internal fun String.removePrefix(prefix: Char): String = if (startsWith(prefix)) drop(1) else this

internal fun String.removePrefix(type: RedisType): String = removePrefix(type.char)
