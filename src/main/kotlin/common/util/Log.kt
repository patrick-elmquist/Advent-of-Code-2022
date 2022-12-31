@file:Suppress("NOTHING_TO_INLINE", "unused")

package common.util

/**
 * Abuse the not operator fun for quick logging
 * If you are reading this, for the love of god, don't use this in production code
 * Example : !"Log this"
 */
operator fun String.not() = println(this)

var loggingEnabled = true

inline fun <T> T.log(): T {
    if (!loggingEnabled) return this
    return this.also { println(it) }
}

inline fun <T> T.log(msg: () -> Any): T {
    if (!loggingEnabled) return this
    return this.also { println(msg()) }
}

inline fun <T> T.log(msg: String): T {
    if (!loggingEnabled) return this
    return this.also { println("$msg $it") }
}
