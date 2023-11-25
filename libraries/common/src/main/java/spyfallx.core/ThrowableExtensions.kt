package spyfallx.core

import kotlin.coroutines.cancellation.CancellationException

fun Throwable.failure(): Try<Nothing> = Failure(this)

fun Any.illegalStateFailure(): Try<Nothing> = Failure(IllegalStateException(this.toString()))

fun Throwable.nonFatalOrThrow(): Throwable =
    if (this.isFatal.not()) this else throw this

val Throwable.isFatal: Boolean
    get() = when (this) {
        is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> true
        else -> false
    }
