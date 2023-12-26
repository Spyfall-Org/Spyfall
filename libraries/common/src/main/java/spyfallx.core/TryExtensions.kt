@file:OptIn(ExperimentalContracts::class)

package spyfallx.core

import spyfallx.core.Try.Failure
import spyfallx.core.Try.Success
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import spyfallx.core.common.BuildConfig
import timber.log.Timber
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

@Suppress("NOTHING_TO_INLINE")
inline fun <T> identity(a: T): T = a

fun <A> A.success(): Try<A> = Success(this)

@Suppress("FunctionNaming")
inline fun <T> Try(f: () -> T): Try<T> = Try.catch(f)

typealias TryDeferred<T> = Deferred<Try<T>>
typealias Failure = Failure
typealias Success<T> = Success<T>

@Suppress("FunctionNaming")
inline fun <T> TryDeferred(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline f: suspend () -> T
): TryDeferred<T> = Try.catchAsync(context, f)

val justUnit: Try<Unit> = Success(Unit)

inline fun <T> Try<T>.getOrElse(default: (Throwable) -> T): T =
    fold({ default(it) }, ::identity)

inline fun <T> Try<T>.defaultOnError(default: () -> T): T =
    fold({ default() }, ::identity)

inline fun <T> Try<T>.flatMapIf(predicate: Boolean, mapper: (T) -> Try<T>): Try<T> =
    if (predicate) flatMap(mapper) else this

/**
 * Returns the list of values from a list of tries. If any of the tries are a failure, the entire
 * list is a failure.
 */
inline fun <T : Any> Collection<Try<T>>.failFast(): Try<List<T>> {
    return Try { this.map { it.getOrThrow() } }
}

fun <T> Try<T>.logOnError(message: String? = null): Try<T> = onFailure { Timber.e(it, message) }

fun <T> Try<T>.throwIfDebug(): Try<T> = onFailure {
    if (BuildConfig.DEBUG && this is Failure) {
        Timber.e("THROWING DEBUG EXCEPTION: ${it.localizedMessage}")
        throw it
    }
}

fun <T> Try<T>.developerSnackOnError(
    autoDismiss: Boolean = false,
    lazyMessage: () -> String,
): Try<T> = onFailure {
    if (BuildConfig.DEBUG && this is Failure) {
        UserMessagePresenter.showDeveloperMessage(
            Message(
                message = lazyMessage(),
                autoDismiss = autoDismiss
            )
        )
    }
}

fun <T> Try<T>.developerSnackOnSuccess(
    autoDismiss: Boolean = true,
    lazyMessage: () -> String,
): Try<T> = onFailure {
    if (BuildConfig.DEBUG && this is spyfallx.core.Success) {
        UserMessagePresenter.showDeveloperMessage(
            Message(
                message = lazyMessage(),
                autoDismiss = autoDismiss
            )
        )
    }
}

fun Throwable.failure(): Try<Nothing> = Failure(this)

fun Any.failure(): Try<Nothing> = Failure(IllegalStateException(this.toString()))

fun illegalState(string: String): Failure = Failure(IllegalStateException(string))

/**
 * Retry an operation a certain number of times with an exponential backoff by default
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun <T> withBackoffRetry(
    retries: Int,
    initialDelayMillis: Long = 0.5.seconds.inWholeMilliseconds,
    maxDelayMillis: Long = 10.seconds.inWholeMilliseconds,
    factor: Double = 2.0,
    block: (attempt: Int) -> Try<T>
): Try<T> {
    contract {
        callsInPlace(block)
    }
    require(retries >= 0)

    var currentDelay = initialDelayMillis

    repeat(retries) {
        when (val result = block(it)) {
            is Success -> return result
            is Failure -> {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
            }
        }
    }

    // Try to execute the operation one more time
    return block(retries)
}

/**
 * Retry an operation a certain number of times with an exponential backoff by default
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> withRetry(
    retries: Int,
    block: () -> Try<T>
): Try<T> {
    contract {
        callsInPlace(block)
    }
    require(retries >= 0)

    repeat(retries) {
        when (val result = block()) {
            is Success -> return result
            is Failure -> doNothing()
        }
    }

    // Try to execute the operation one more time
    return block()
}

fun <A, B, C> unwrapTrys(
    tryOne: Try<A>,
    tryTwo: Try<B>,
    handler: (A, B) -> C,
): Try<C> =
    tryOne
        .flatMapError {
            tryTwo.getExceptionOrNull()?.let(it::addSuppressed)
            Try.raise(it)
        }
        .flatMap { resultOne ->
            tryTwo.map { resultTwo ->
                handler(resultOne, resultTwo)
            }
        }

inline fun <T> Try<T>.flatMapError(f: (left: Throwable) -> Try<T>): Try<T> {
    contract {
        callsInPlace(f, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Success -> Success(value)
        is Failure -> f(exception)
    }
}

fun <T> List<Try<List<T>>>.getFirstErrorOrCombineResults(): Try<List<T>> =
    Try.just(
        buildList {
            for (item in this@getFirstErrorOrCombineResults) {
                addAll(
                    when (item) {
                        is Failure -> return item
                        is Success -> item.value
                    }
                )
            }
        }
    )

suspend inline fun <A, B> Collection<A>.mapEachSuccess(
    attemptAll: Boolean,
    crossinline block: suspend (A) -> B
): Try<List<B>> {
    val results = this.map { item ->
        val result = Try { block(item) }
        if (result is Failure) {
            if (attemptAll.not()) {
                return result
            }
        }
        result
    }

    // Even if we did attempt all, return the first failure. Otherwise get all successful results
    return results.getFirstFailureOrAllSuccessful()
}

fun <T> List<Try<T>>.getFirstFailureOrAllSuccessful(): Try<List<T>> {
    val firstFailure = this.firstOrNull { it is Failure }
    if (firstFailure != null && firstFailure is Failure) return firstFailure
    return Try { this.map { it.getOrThrow() } }
}

@JvmName("getFirstErrorOrCombineResultsMap")
fun <K, V> List<Try<Map<K, V>>>.getFirstErrorOrCombineResults(): Try<Map<K, V>> =
    Try.just(
        buildMap {
            for (item in this@getFirstErrorOrCombineResults) {
                putAll(
                    when (item) {
                        is Failure -> return item
                        is Success -> item.value
                    }
                )
            }
        }
    )

/**
 * Merges the receiver with the [Try] returned from [block].
 *
 * If the receiver is an error, it is returned without calling [block].
 * If the receiver is success then [block] is called.
 *
 * If block result is an error then the block result is returned.
 * If block result is success then the two Trys are merged into a [Pair].
 */
inline fun <T, R> Try<T>.pairBothWrapMap(block: (T) -> Try<R>): Try<Pair<T, R>> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return flatMap { result -> block(result).map { result to it } }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Try<Try<T>>.flatten(): Try<T> {
    return this.flatMap { innerTry ->
        when (innerTry) {
            is Success -> Success(innerTry.value)
            is Failure -> Failure(innerTry.exception)
        }
    }
}

inline fun <reified T> Iterable<*>.filterIsInstance(): List<T> {
    return filterIsInstanceTo(ArrayList<T>())
}

inline fun <reified Input, Output : MutableCollection<in Input>> Iterable<*>.filterIsInstanceTo(
    destination: Output
): Output {
    for (element in this) if (element is Input) destination.add(element)
    return destination
}

@Suppress("UNCHECKED_CAST")
inline fun <K, reified R> Map<K, *>.filterIsInstance(): Map<K, R> =
    filterValues { it is R } as Map<K, R>

/**
 * Lazily evaluates each block in a List and exits early or continues evaluating based on error logic parameters.
 * By default nothing is recoverable.
 */
inline fun <T> List<T>.firstFailureOrEmpty(
    isRecoverable: (Throwable) -> Boolean = { false },
    block: (T) -> Try<Unit>
): Try<Unit> {
    var error: Throwable? = null
    forEach { item ->

        // Lazily trying to evaluate each block
        val nextError: Throwable? =
            // Just because `block` returns a Try<Unit> doesn't mean it won't throw an exception - this handles that case
            Try { block(item) }.flatten()
                // Since we're using Try<Unit> as the result type we can ignore whatever `T` was
                .ignoreValue()
                // Determine whether to exit early and stop attempting the rest of the List
                .onFailure { result ->
                    if (!isRecoverable(result)) {
                        return result.failure()
                    }
                }
                // We need the Exception if it exits
                .getExceptionOrNull()

        // We want to keep track of the first recoverable error we encountered while allowing other attempts.
        error = error ?: nextError
    }

    return error?.let { Failure(it) } ?: Success(Unit)
}
