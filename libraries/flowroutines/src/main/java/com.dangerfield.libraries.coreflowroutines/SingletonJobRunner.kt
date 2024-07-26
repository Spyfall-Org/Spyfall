package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Suppress("UnusedPrivateMember")
class SingletonJobRunner(
    scope: CoroutineScope,
    private val job: suspend () -> Unit,
) {
    private var value: Unit? = null
    private val mutex = Mutex()

    init {
        scope.launch {
            join()
        }
    }

    suspend fun join() {
        if (value == null) {
            mutex.withLock {
                /*
                double check value inside lock as the mutex.withLock could have suspended
                while the value was getting set
                 */
                if (value == null) {
                    value = job()
                }
            }
        }
    }
}
