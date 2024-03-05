package oddoneout.core

import kotlinx.coroutines.TimeoutCancellationException
import spyfallx.core.common.BuildConfig
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException


val Throwable.shouldNotBeCaught: Boolean
    get() = when {
        isThrowableCancellation()
                || this is VirtualMachineError
                || this is ThreadDeath
                || this is InterruptedException
                || this is LinkageError -> true
        else -> false
    }

private fun Throwable.isThrowableCancellation() =
    this is CancellationException && this !is TimeoutCancellationException

class DebugException(e: Throwable? = null, message: String? = e?.localizedMessage) :
    Exception(message, e)

fun throwIfDebug(throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        throw DebugException(message = throwable.message.orEmpty())
    }
}

fun throwIfDebug(lazyMessage: () -> Any) {
    if (BuildConfig.DEBUG) {
        throw DebugException(message = lazyMessage().toString())
    }
    Timber.e(lazyMessage().toString())
}

fun developerSnackIfDebug(
    autoDismiss: Boolean = false,
    lazyMessage: () -> Any
) {
    if (BuildConfig.DEBUG) {
        SnackBarPresenter.showDeveloperMessage(
            Message(
                message = lazyMessage().toString(),
                autoDismiss = autoDismiss
            )
        )
    }
    Timber.i(lazyMessage().toString())
}

inline fun checkInDebug(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        if (BuildConfig.DEBUG) throw DebugException(message = lazyMessage().toString())
    }
}