package spyfallx.core

import spyfallx.core.common.BuildConfig
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException


fun Throwable.nonFatalOrThrow(): Throwable = if (this.isFatal.not()) this else throw this

val Throwable.isFatal: Boolean
    get() = when (this) {
        is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> true
        else -> false
    }

class DebugException(message: String) : Exception(message)

fun throwIfDebug(throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        val throwable = DebugException(throwable.message.orEmpty())
        throw throwable
    }
}

fun throwIfDebug(lazyMessage: () -> Any) {
    val message = lazyMessage()
    if (BuildConfig.DEBUG) {
        val throwable = DebugException(message.toString())
        throw throwable
    }
    Timber.e(message.toString())
}

fun developerSnackIfDebug(
    autoDismiss: Boolean = false,
    lazyMessage: () -> Any
) {
    val message = lazyMessage()
    if (BuildConfig.DEBUG) {
        UserMessagePresenter.showDeveloperMessage(
            Message(
                message = lazyMessage().toString(), autoDismiss = autoDismiss
            )
        )
    }
    Timber.i(message.toString())
}

inline fun checkInDebug(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        val message = lazyMessage()
        val throwable = DebugException(message.toString())
        if (BuildConfig.DEBUG) throw throwable
    }
}