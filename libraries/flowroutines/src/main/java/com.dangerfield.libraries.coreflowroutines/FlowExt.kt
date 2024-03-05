package com.dangerfield.libraries.coreflowroutines

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Collects the flow and calls the collector with the previous and current value.
 * Useful when you want to compare the previous and current value.
 */
suspend fun <T : Any> Flow<T>.collect(collector: suspend (previous: T?, current: T) -> Unit) {
    var previous: T? = null
    collect {
        collector(previous, it)
        previous = it
    }
}

fun <T> Flow<T>.collectIn(scope: CoroutineScope, collector: FlowCollector<T>): Job = scope.launch { collect(collector) }

suspend fun <T> Flow<T>.waitFor(preditcate: (T) -> Boolean): T {
    return this.first { preditcate(it) }
}

/**
 * Allows launching a job in a new coroutine when collection starts on a flow.
 * This is similar to [onStart] but with the difference that you can launch a new coroutine and still emit values.
 *
 * with [onStart] you can only emit values from the coroutine that is collecting the flow.
 *
 * Just as with [onStart] The [job] is called before the upstream flow is started, so if it is used with a [SharedFlow]
 * there is **no guarantee** that emissions from the upstream flow that happen inside or immediately
 * after this `onStart` action will be collected as there may not be an active collector at that time.
 *
 * This operator is very useful if you want to listen to a database but start a network refresh when collection starts:
 * ```
 * database.someDao.observeData()
 *   .launchOnStart {
 *     refreshDataFromNetwork()
 *   }
 * ```
 *
 *
 */
fun <T> Flow<T>.launchOnStart(job: suspend kotlinx.coroutines.channels.ProducerScope<T>.() -> Unit): Flow<T> =
    channelFlow {
        launch {
            // This is wrapped so that the coroutine scope is the launched scope while the send channel is from the
            // channelFlow
            ProducerScope<T>(this, this@channelFlow.channel).job()
        }
        collect(::send)
    }.buffer(Channel.RENDEZVOUS)

private class ProducerScope<E>(
    scope: CoroutineScope,
    override val channel: SendChannel<E>,
) : kotlinx.coroutines.channels.ProducerScope<E>, CoroutineScope by scope, SendChannel<E> by channel


/**
 * Flow that emits one value from the given block
 */
fun <T> flowOf(block: suspend () -> T): Flow<T> = flow {
    emit(block())
}
