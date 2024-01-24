package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import oddoneout.core.Failure
import oddoneout.core.Success
import oddoneout.core.Try
import oddoneout.core.defaultOnError
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

val cancelledJob: Job = Job().apply { cancel() }
val cancelledScope = CoroutineScope(cancelledJob)

/**
 * Creates a new [CoroutineScope] that is a "child" of the receiver.
 *
 * A child scope is a scope that will be canceled when the parent scope is canceled.
 *
 * The [context] of the new scope is the receiver's context plus the given [context]. The context must not contains a
 * job since one will be created for the new scope.
 *
 * This scope will use a [Job] as its job. If you want to use a [SupervisorJob], use [childSupervisorScope] instead.
 * Regular jobs will be canceled if any of the children fail, but supervisor jobs are not.
 *
 * @receiver The parent scope
 * @param context The context to add to the parent's context. Defaults to [EmptyCoroutineContext].
 * @return The new child scope
 * @throws IllegalArgumentException If the given [context] contains a [Job].
 * @see childSupervisorScope
 */
fun CoroutineScope.childScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
    childScopeInternal(context, ::Job)

/**
 * Creates a new [CoroutineScope] that is a "child" of the receiver.
 *
 * A child scope is a scope that will be canceled when the parent scope is canceled.
 *
 * The [context] of the new scope is the receiver's context plus the given [context]. The context must not contains a
 * job since one will be created for the new scope.
 *
 * This scope will use a [SupervisorJob] as its job. If you want to use a [Job], use [childScope] instead.
 * Supervisor jobs are useful when launching multiple, independent, coroutines. Normal jobs are canceled if any of the
 * children fail, but supervisor jobs are not.
 *
 * See [SupervisorJob] for more information on this behavior.
 *
 * @receiver The parent scope
 * @param context The context to add to the parent's context. Defaults to [EmptyCoroutineContext].
 * @return The new child scope
 * @throws IllegalArgumentException If the given [context] contains a [Job].
 * @see childScope
 */
fun CoroutineScope.childSupervisorScope(context: CoroutineContext = EmptyCoroutineContext): CoroutineScope =
    childScopeInternal(context, ::SupervisorJob)


private inline fun CoroutineScope.childScopeInternal(
    context: CoroutineContext,
    job: (Job?) -> Job
): CoroutineScope {
    require(context[Job] == null) {
        "You cannot pass a job, it will be created for you"
    }
    return CoroutineScope(coroutineContext + context + job(coroutineContext[Job]))
}

/**
 * Calls the given [block] and Fails with [TimeoutCancellationException] if the block does not complete
 * within the specified [duration].
 */
@OptIn(ExperimentalContracts::class)
suspend fun <T> tryWithTimeout(
    duration: Duration,
    block: suspend CoroutineScope.() -> Try<T>
): Try<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return try {
        withTimeout(duration, block)
    } catch (e: TimeoutCancellationException) {
        Try.Failure(e)
    }
}

fun <A> Flow<Try<A>>.defaultOnError(default: () -> A): Flow<A> = map { it.defaultOnError(default) }

fun <A, B> Flow<Try<A>>.flatMapTry(success: (A) -> Flow<Try<B>>): Flow<Try<B>> {
    return flatMapLatest { result ->
        when (result) {
            is Success -> success(result.value)
            is Failure -> kotlinx.coroutines.flow.flowOf(Failure(result.exception))
        }
    }
}

fun <T> Flow<T>.mapTry(): Flow<Try<T>> {
    return this.map { Try { it } }.catch { emit(Failure(it)) }
}

fun <T, R> Flow<Try<T>>.wrapMap(mapper: suspend (T) -> Try<R>): Flow<Try<R>> {
    return map { item ->
        when (item) {
            is Success -> mapper(item.value)
            is Failure -> Try.raise(item.exception)
        }
    }
}

/**
 * [transform] will map using the previous emission from the upstream flow and the current, most recent emission
 */
fun <T : Any, R> Flow<T>.mapWithPrevious(transform: (oldItem: T?, newItem: T) -> R): Flow<R> =
    mapWithPrevious(null, transform)

/**
 * [transform] will map using the previous emission from the upstream flow and the current, most recent emission.
 *
 * For the first item then [initialItem] will be used
 */
fun <I, T : I, R> Flow<T>.mapWithPrevious(
    initialItem: I,
    transform: (oldItem: I, newItem: T) -> R
): Flow<R> = flow {
    var previous: I = initialItem
    collect { value ->
        emit(transform(previous, value))
        previous = value
    }
}
