package oddoneout.core

import android.util.Log
import spyfallx.core.common.BuildConfig
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

/**
 * We do not catch fatals, they should be considered not ignorable.
 */
fun Throwable.nonFatalOrThrow(): Throwable = if (this.isFatal.not()) {
    this
} else {
    Log.d("Elijah", "Throwing fatal error from a try")
    Timber.i("Throwing error from Try because its fatal")
    throw this
}

val Throwable.isFatal: Boolean
    get() = when (this) {
        is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> true
        else -> false
    }

class DebugException(message: String) : Exception(message)

fun throwIfDebug(throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        throw DebugException(throwable.message.orEmpty())
    }
}

fun throwIfDebug(lazyMessage: () -> Any) {
    if (BuildConfig.DEBUG) {
        throw DebugException(lazyMessage().toString())
    }
    Timber.e(lazyMessage().toString())
}

fun developerSnackIfDebug(
    autoDismiss: Boolean = false,
    lazyMessage: () -> Any
) {
    if (BuildConfig.DEBUG) {
        UserMessagePresenter.showDeveloperMessage(
            Message(
                message = lazyMessage().toString(), autoDismiss = autoDismiss
            )
        )
    }
    Timber.i(lazyMessage().toString())
}

inline fun checkInDebug(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        if (BuildConfig.DEBUG) throw DebugException(lazyMessage().toString())
    }
}