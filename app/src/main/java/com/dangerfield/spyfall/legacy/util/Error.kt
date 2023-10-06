package com.dangerfield.spyfall.legacy.util

inline fun <reified T> Throwable.removeClassFromStackTrace() = removeClassFromStackTrace(T::class.java)

/**
 * Removes the given [Class] from the stack trace for this throwable.
 *
 * This is useful when you want to prevent grouping of exceptions that happen from different places in the app but where
 * the exception is thrown by common code.
 *
 * For example, given this stack trace:
 * ```
 * Fatal Exception: java.lang.IllegalStateException
 *   at co.hinge.core.android.ui.view.ManagedViewPropertyDelegate.getValue(ManagedViewPropertyDelegate.kt:57)
 *   at co.hinge.discover.ui.DiscoverFragment.getUi(DiscoverFragment.kt:7)
 * ```
 *
 * Calling this function with `ManagedViewPropertyDelegate` as the class will update the stacktrace to be:
 * ```
 * Fatal Exception: java.lang.IllegalStateException
 *   at co.hinge.discover.ui.DiscoverFragment.getUi(DiscoverFragment.kt:7)
 * ```
 *
 * This function can remove multiple lines from the stack trace, but it will only remove the first lines. If the
 * class is present in the middle of the stack trace, they are not removed as it would make it extremely hard to follow
 * the stack trace.
 *
 * @return The receiver for better ergonomics
 */
fun Throwable.removeClassFromStackTrace(clazz: Class<*>): Throwable = apply {
    val stackTrace = stackTrace
    var line = 0
    while (line < stackTrace.size) {
        val element = stackTrace[line]
        if (element.className == clazz.name) {
            ++line
        } else {
            break
        }
    }
    if (line == 0) {
        return this
    }
    this.stackTrace = stackTrace.copyOfRange(line, stackTrace.size)
}
