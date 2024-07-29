package oddoneout.core

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import spyfallx.core.common.BuildConfig
import timber.log.Timber
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration.Companion.seconds

typealias Catching<T> = Result<T>

inline fun <T> Catching(f: () -> T): Catching<T> = runCatching(f)
    .onFailure {
        if (it.shouldNotBeCaught) throw it
    }

fun <T> Catching<T>.logOnFailure(message: String? = null): Catching<T> = onFailure {
    Timber.e(DebugException(it, message))
}

fun <T> Catching<T>.getExceptionOrNull(): Throwable? = exceptionOrNull()

fun <T> Catching<T>.throwIfDebug(): Catching<T> = onFailure {
    if (BuildConfig.DEBUG && this.isFailure) {
        throw DebugException(it)
    }
}

/**
 * Shows a snack describing the error ff the result is a failure and the build is Debug
 */
fun <T> Catching<T>.debugSnackOnError(
    autoDismiss: Boolean = false,
    lazyMessage: () -> String,
): Catching<T> = onFailure {
    if (BuildConfig.DEBUG && this.isFailure) {
        SnackBarPresenter.showDebugMessage(
            Message(
                message = lazyMessage(),
                autoDismiss = autoDismiss
            )
        )
    }
}

/**
 * Shows a snack describing the error ff the result is a failure
 */
fun <T> Catching<T>.snackOnError(
    autoDismiss: Boolean = false,
    lazyMessage: () -> String,
): Catching<T> = onFailure {
    if (this.isFailure) {
        SnackBarPresenter.showMessage(
            Message(
                message = lazyMessage(),
                autoDismiss = autoDismiss
            )
        )
    }
}

inline fun illegalStateFailure(lazyMessage: () -> String) =
    Catching.failure<Nothing>(IllegalStateException(lazyMessage()))

fun <T> Flow<T>.asCatching(): Flow<Catching<T>> = map {
    Catching { it }
}.catch { emit(Result.failure(it)) }

suspend fun <T> Task<T>.awaitCatching(): Catching<T> = Catching {
    await()
}

fun <T> T.success(): Catching<T> = Catching.success(this)
fun success(): Catching<Unit> = Catching.success(Unit)

fun failure(throwable: Throwable): Catching<Nothing> = Catching.failure(throwable)

inline fun <T> Catching<T>.eitherWay(block: (Catching<T>) -> Unit) = this.also(block)

fun <T> Catching<T>.ignoreValue(): Catching<Unit> = this.map { }
fun <T> Catching<T>.ignore() = doNothing()

inline fun <T> Catching<T>.fold(
    ifFailure: (left: Throwable) -> T,
    ifSuccess: (right: T) -> T
): T {
    val value = this.getOrNull()
    val exception = this.getExceptionOrNull()

    return when  {
        value != null -> ifSuccess(value)
        exception != null -> ifFailure(exception)
        else -> throw IllegalStateException("Catching is neither success nor failure")
    }
}

inline fun <T> Catching<T>.mapFailure(f: (Throwable) -> Throwable): Catching<T> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> Result.failure(f(exception))
    }
}

fun <T> Flow<T>.mapTry(): Flow<Catching<T>> {
    return this.map { Catching { it } }.catch { emit(failure(it)) }
}

inline fun <T> List<Catching<T>>.failFast(): Catching<T> {
    return this.firstOrNull() { it.isFailure } ?: this.last()
}

inline fun <T> List<Catching<T>>.allOrElse(f: () -> List<T>): List<T> {
    val results = this.mapNotNull { it.getOrNull() }
    return if (results.size != this.size) {
       f()
    } else {
        results
    }
}

inline fun <T, R> Catching<T>.flatMap(f: (right: T) -> Catching<R>): Catching<R> {
    val exception = exceptionOrNull()
    return when {
        exception != null -> Catching.failure(exception)
        else -> f(getOrThrow())
    }
}

/**
 * Retry an operation a certain number of times with an exponential backoff by default
 */
suspend inline fun <T> withBackoffRetry(
    retries: Int = 0,
    initialDelayMillis: Long = 0.5.seconds.inWholeMilliseconds,
    maxDelayMillis: Long = 10.seconds.inWholeMilliseconds,
    factor: Double = 2.0,
    block: (attempt: Int) -> Catching<T>
): Catching<T> {

    var currentDelay = initialDelayMillis

    repeat(retries) {
        val result = block(it)
        when {
            result.isSuccess -> return result
            result.isFailure -> {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
            }
        }
    }

    return block(retries)
}