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

suspend fun <T> tryWithTimeout(
    duration: Duration,
    block: suspend CoroutineScope.() -> Catching<T>
): Catching<T> {
    return try {
        withTimeout(duration, block)
    } catch (e: TimeoutCancellationException) {
        Catching.failure(e)
    }
}