package com.dangerfield.libraries.flowroutines

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.concurrent.CancellationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun <T> Flow<T>.mapResult(): Flow<Result<T>> {
    return this.map { runCancellableCatching { it } }.catch { emit(Result.failure(it)) }
}

/**
 * Retry an operation a certain number of times.
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> withRetry(retries: Int, block: () -> Result<T>): Result<T> {
    contract {
        callsInPlace(block)
    }
    require(retries >= 0)
    repeat(retries) {
        val result = block()
        if (result.isSuccess) {
            return result
        } else {
            // continue
        }
    }
    // Try to execute the operation one more time
    return block()
}

/**
 * utility function to wrap asynchronous code with error catching
 * and return result wrapped in a kotlin [Result]
 *
 * does not catch [CancellationException] other than [TimeoutCancellationException]
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <R> runCancellableCatching(block: suspend () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (t: TimeoutCancellationException) {
        Result.failure(t)
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }

