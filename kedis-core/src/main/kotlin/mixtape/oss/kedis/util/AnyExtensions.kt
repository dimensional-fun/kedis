package mixtape.oss.kedis.util

internal inline fun <reified T> Any.into(): T = this as T
