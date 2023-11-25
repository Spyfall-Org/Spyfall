@file:OptIn(ExperimentalContracts::class)

package spyfallx.core

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

    @OptIn(ExperimentalContracts::class)
    inline fun <Output> fold(
        ifFailure: (left: Throwable) -> Output,
        ifSuccess: (right: T) -> Output
    ): Output {
        contract {
            callsInPlace(ifFailure, InvocationKind.AT_MOST_ONCE)
            callsInPlace(ifSuccess, InvocationKind.AT_MOST_ONCE)
        }
        return when (this) {
            is Success -> ifSuccess(value)
            is Failure -> ifFailure(exception)
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
    inline fun <Output> flatMap(f: (right: T) -> Try<Output>): Try<Output> {
        contract {
            callsInPlace(f, InvocationKind.AT_MOST_ONCE)
        }
        return when (this) {
            is Success -> f(value)
            is Failure -> Failure(exception)
        }
    }

    inline fun <Output> map(f: (right: T) -> Output): Try<Output> {
        contract {
            callsInPlace(f, InvocationKind.AT_MOST_ONCE)
        }
        return flatMap { Success(f(it)) }
    }

    inline fun <Output> mapNotNull(f: (right: T) -> Output?): Try<Output> {
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

        override fun toString(): String = "Try.Failure($exception)"
    }

    data class Success<B> constructor(val value: B) : Try<B>() {
        override val isSuccess = true

        override fun ignoreValue(): Try<Unit> = Success(Unit)

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

        suspend inline fun <InputA, InputB, Output> parTraverseEither(
            deferredA: TryDeferred<InputA>,
            deferredB: TryDeferred<InputB>,
            handler: (InputA, InputB) -> Output
        ): Try<Output> {
            val tryA = deferredA.await()
            val tryB = deferredB.await()
            return when {
                tryA is Failure -> tryA
                tryB is Failure -> tryB
                else -> handler(
                    tryA.getOrThrow(),
                    tryB.getOrThrow()
                ).success()
            }
        }

        suspend inline fun <InputA, InputB, Output> parZip(
            deferredA: TryDeferred<InputA>,
            deferredB: TryDeferred<InputB>,
            handler: (InputA, InputB) -> Output
        ): Try<Output> {
            val tryA = deferredA.await()
            val tryB = deferredB.await()
            return when {
                tryA is Failure -> tryA
                tryB is Failure -> tryB
                else -> handler(
                    tryA.getOrThrow(),
                    tryB.getOrThrow()
                ).success()
            }
        }

        suspend inline fun <InputA, InputB, InputC, Output> parZip(
            deferredA: TryDeferred<InputA>,
            deferredB: TryDeferred<InputB>,
            deferredC: TryDeferred<InputC>,
            handler: (InputA, InputB, InputC) -> Output
        ): Try<Output> {
            val tryA = deferredA.await()
            val tryB = deferredB.await()
            val tryC = deferredC.await()
            return when {
                tryA is Failure -> tryA
                tryB is Failure -> tryB
                tryC is Failure -> tryC
                else -> handler(
                    tryA.getOrThrow(),
                    tryB.getOrThrow(),
                    tryC.getOrThrow()
                ).success()
            }
        }

        suspend inline fun <InputA, InputB, InputC, InputD, Output> parZip(
            deferredA: TryDeferred<InputA>,
            deferredB: TryDeferred<InputB>,
            deferredC: TryDeferred<InputC>,
            deferredD: TryDeferred<InputD>,
            handler: (InputA, InputB, InputC, InputD) -> Output
        ): Try<Output> {
            val tryA = deferredA.await()
            val tryB = deferredB.await()
            val tryC = deferredC.await()
            val tryD = deferredD.await()
            return when {
                tryA is Failure -> tryA
                tryB is Failure -> tryB
                tryC is Failure -> tryC
                tryD is Failure -> tryD
                else -> handler(
                    tryA.getOrThrow(),
                    tryB.getOrThrow(),
                    tryC.getOrThrow(),
                    tryD.getOrThrow()
                ).success()
            }
        }
    }

    inline fun validate(validation: (T) -> Unit): Try<T> = flatMap {
        Try {
            validation(it)
            it
        }
    }

    abstract fun ignoreValue(): Try<Unit>
}
