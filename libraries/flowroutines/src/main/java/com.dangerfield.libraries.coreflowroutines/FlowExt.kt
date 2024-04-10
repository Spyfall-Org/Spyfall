package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

suspend fun <T : Any> Flow<T>.collectInWithPrevious(
    scope: CoroutineScope,
    collector: suspend (previous: T?, current: T) -> Unit
) {
    scope.launch {
        var previous: T? = null
        collect {
            collector(previous, it)
            previous = it
        }
    }
}

fun <T> Flow<T>.collectIn(scope: CoroutineScope, collector: FlowCollector<T>): Job =
    scope.launch { collect(collector) }

suspend fun <T> Flow<T>.waitFor(preditcate: (T) -> Boolean): T {
    return this.first { preditcate(it) }
}

fun <T> Flow<T>.onCollection(job: suspend kotlinx.coroutines.channels.ProducerScope<T>.() -> Unit) =
    channelFlow {
        launch {
            ProducerScope<T>(this, this@channelFlow.channel).job()
        }
        collect(::send)
    }
        .buffer(Channel.RENDEZVOUS) // suspends each send until the previous one is received

private class ProducerScope<E>(
    scope: CoroutineScope,
    override val channel: SendChannel<E>,
) : kotlinx.coroutines.channels.ProducerScope<E>, CoroutineScope by scope, SendChannel<E> by channel
