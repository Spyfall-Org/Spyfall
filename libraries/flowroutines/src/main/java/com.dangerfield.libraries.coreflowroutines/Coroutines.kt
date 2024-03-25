package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import oddoneout.core.Catching
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

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
    block: suspend CoroutineScope.() -> Catching<T>
): Catching<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return try {
        withTimeout(duration, block)
    } catch (e: TimeoutCancellationException) {
        Catching.failure(e)
    }
}