package oddoneout.core

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import spyfallx.core.common.BuildConfig
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

inline fun <T> Try(f: () -> T): Try<T> = runCatching(f)
    .onFailure {
        if (it.shouldNotBeCaught) throw it
    }

fun <T> Try<T>.logOnFailure(message: String? = null): Try<T> = onFailure {
    Timber.e(DebugException(it, message))
}

fun <T> Try<T>.throwIfDebug(): Try<T> = onFailure {
    if (BuildConfig.DEBUG && this.isFailure) {
        throw DebugException(it)
    }
}

fun <T> Try<T>.developerSnackOnError(
    autoDismiss: Boolean = false,
    lazyMessage: () -> String,
): Try<T> = onFailure {
    if (BuildConfig.DEBUG && this.isFailure) {
        SnackBarPresenter.showDeveloperMessage(
            Message(
                message = lazyMessage(),
                autoDismiss = autoDismiss
            )
        )
    }
}

inline fun illegalStateFailure(lazyMessage: () -> String) =
    Try.failure<Nothing>(IllegalStateException(lazyMessage()))

fun <T> Flow<T>.asResult(): Flow<Try<T>> = map {
    Try { it }
}.catch { emit(Result.failure(it)) }

suspend fun <T> Task<T>.awaitResult(): Try<T> = Try {
    await()
}

fun <T> T.success(): Try<T> = Try.success(this)
fun success(): Try<Unit> = Try.success(Unit)

fun failure(throwable: Throwable): Try<Nothing> = Try.failure(throwable)

inline fun <T> Try<T>.eitherWay(block: (Try<T>) -> Unit) = this.also(block)

fun <T> Try<T>.ignoreValue(): Try<Unit> = this.map { }
fun <T> Try<T>.ignore() = doNothing()

inline fun <T> Try<T>.mapFailure(f: (Throwable) -> Throwable): Try<T> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> Result.failure(f(exception))
    }
}

fun <T> Flow<T>.mapTry(): Flow<Try<T>> {
    return this.map { Try { it } }.catch { emit(failure(it)) }
}

inline fun <T> List<Try<T>>.failFast(): Try<T> {
    return this.firstOrNull() { it.isFailure } ?: this.last()
}

inline fun <T, R> Try<T>.flatMap(f: (right: T) -> Try<R>): Try<R> {
    val exception = exceptionOrNull()
    return when {
        exception != null -> Try.failure(exception)
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
    block: (attempt: Int) -> Try<T>
): Try<T> {

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