@file:OptIn(ExperimentalContracts::class)

package oddoneout.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

sealed class Try<out T> {

    abstract val isSuccess: Boolean
    val isFailure: Boolean
        get() = !isSuccess

    inline fun <R> fold(
        onFailure: (left: Throwable) -> R,
        onSuccess: (right: T) -> R
    ): R {

        return when (this) {
            is Success -> onSuccess(value)
            is Failure -> onFailure(exception)
        }
    }

    inline fun handle(
        success: (T) -> Unit,
        error: (Throwable) -> Unit,
    ) {
        contract {
            callsInPlace(success, InvocationKind.AT_MOST_ONCE)
            callsInPlace(error, InvocationKind.AT_MOST_ONCE)
        }
        fold(error, success)
    }

    inline fun <B> mapTry(
        successHandler: (T) -> B,
        errorHandler: (Throwable) -> B,
    ): B = fold(errorHandler, successHandler)

    @OptIn(ExperimentalContracts::class)
    inline fun <R> flatMap(f: (right: T) -> Try<R>): Try<R> {
        contract {
            callsInPlace(f, InvocationKind.AT_MOST_ONCE)
        }
        return when (this) {
            is Success -> f(value)
            is Failure -> Failure(exception)
        }
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <R> map(f: (right: T) -> R): Try<R> {
        contract {
            callsInPlace(f, InvocationKind.AT_MOST_ONCE)
        }
        return flatMap { Success(f(it)) }
    }

    inline fun <R> mapNotNull(f: (right: T) -> R?): Try<R> {
        contract {
            callsInPlace(f, InvocationKind.AT_MOST_ONCE)
        }
        return when (this) {
            is Success -> value?.let { f(it)?.success() } ?: Failure(NullPointerException("Try.Success.value was null"))
            is Failure -> Failure(exception)
        }
    }

    inline fun mapFailure(f: (Throwable) -> Throwable): Try<T> {
        contract {
            callsInPlace(f, InvocationKind.AT_MOST_ONCE)
        }
        return fold({ Failure(f(it)) }, { Success(it) })
    }

    inline fun onSuccess(action: (right: T) -> Unit): Try<T> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }
        return also { if (it is Success) action(it.value) }
    }

    @OptIn(ExperimentalContracts::class)
    inline fun onFailure(action: (left: Throwable) -> Unit): Try<T> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }
        return also { if (it is Failure) action(it.exception) }
    }

    @OptIn(ExperimentalContracts::class)
    inline fun onTimeOut(action: (left: Throwable) -> Unit): Try<T> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }
        return also { if (it is Failure && it.exception is TimeoutCancellationException) action(it.exception) }
    }

    @OptIn(ExperimentalContracts::class)
    inline fun finally(action: (e: Throwable?, t: T?) -> Unit): Try<T> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }

        return also {
            if (it is Failure) {
                action(it.exception, null)
            } else if (it is Success) {
                action(null, it.value)
            }
        }
    }


    @OptIn(ExperimentalContracts::class)
    inline fun eitherWay(action: () -> Unit): Try<T> {
        contract {
            callsInPlace(action, InvocationKind.AT_MOST_ONCE)
        }

        return also {
            action()
        }
    }

    /**
     * Returns a new [Try] that maps a successful value to another value using the [mapper].
     *
     * This is similar to [map][Try.map] except that any exceptions thrown from [mapper] will be caught and wrapped in a
     * [Try].
     */
    @OptIn(ExperimentalContracts::class)
    inline fun <Output> mapCatching(mapper: (T) -> Output): Try<Output> {
        contract { callsInPlace(mapper, InvocationKind.AT_MOST_ONCE) }
        return flatMap { Try { mapper(it) } }
    }

    fun getOrNull(): T? =
        fold({ null }, ::identity)

    fun getExceptionOrNull(): Throwable? =
        (this as? Failure)?.exception

    fun getOrThrow(): T =
        fold({ throw it }, { it })

    data class Failure constructor(val exception: Throwable) : Try<Nothing>() {
        override val isSuccess = false

        override fun ignoreValue(): Try<Unit> = this

        override fun ignoreEverything(): Unit = Unit

        override fun toString(): String = "Try.Failure($exception)"
    }

    data class Success<B> constructor(val value: B) : Try<B>() {
        override val isSuccess = true

        override fun ignoreValue(): Try<Unit> = Success(Unit)

        override fun ignoreEverything(): Unit = Unit

        override fun toString(): String = "Try.Success($value)"
    }

    companion object {

        @Suppress("NOTHING_TO_INLINE")
        inline fun <T> just(value: T): Try<T> = Success(value)

        @Suppress("NOTHING_TO_INLINE")
        inline fun <T> raise(error: Throwable): Try<T> = Failure(error)

        @JvmName("tryCatch")
        @Suppress("TooGenericExceptionCaught")
        inline fun <T> catch(f: () -> T): Try<T> =
            try {
                f().success()
            } catch (t: Throwable) {
                t.nonFatalOrThrow().failure()
            }

        @JvmName("tryCatch")
        @Suppress("TooGenericExceptionCaught")
        inline fun <T> catchAsync(context: CoroutineContext, crossinline f: suspend () -> T): TryDeferred<T> {
            return CoroutineScope(context).async {
                try {
                    f().success()
                } catch (t: Throwable) {
                    t.nonFatalOrThrow().failure()
                }
            }
        }
    }

    abstract fun ignoreValue(): Try<Unit>

    abstract fun ignoreEverything(): Unit

}
