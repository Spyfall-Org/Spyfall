package spyfallx.core

import spyfallx.core.common.BuildConfig
import timber.log.Timber
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException


fun Throwable.nonFatalOrThrow(): Throwable =
    if (this.isFatal.not()) this else throw this

val Throwable.isFatal: Boolean
    get() = when (this) {
        is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> true
        else -> false
    }

fun throwIfDebug(throwable: Throwable) {
    if (BuildConfig.DEBUG) throw throwable
}

fun throwIfDebug(lazyMessage: () -> Any) {
    val message = lazyMessage()
    if (BuildConfig.DEBUG) {
        val throwable = IllegalStateException(message.toString())
        throw throwable
    }
    Timber.e(message.toString())
}

inline fun checkInDebug(value: Boolean, lazyMessage: () -> Any): Unit {
    if (!value) {
        val message = lazyMessage()
        val throwable = IllegalStateException(message.toString())
        Timber.e(throwable)
        if (BuildConfig.DEBUG) throw throwable
    }
}