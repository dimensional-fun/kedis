package mixtape.oss.kedis.util

internal val String.escaped: String
    get() = replace("\r", "\\r").replace("\n", "\\n")

internal fun String.containsLF(): Boolean =
    any { it == '\n' }

internal fun String.containsCR(): Boolean =
    any { it == '\r' }
