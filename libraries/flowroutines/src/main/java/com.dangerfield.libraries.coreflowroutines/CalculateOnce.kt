package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Suppress("UnusedPrivateMember")
class CalculateOnce<T>(
    scope: CoroutineScope,
    private val initializer: suspend () -> T,
) {
    private var value: T? = null
    private val mutex = Mutex()

    init {
        scope.launch {
            getValue()
        }
    }

    suspend fun getValue(): T {
        if (value == null) {
            mutex.withLock {
                /*
                double check value inside lock as the mutex.withLock could have suspended
                while the value was getting set
                 */
                if (value == null) {
                    value = initializer()
                }
            }
        }
        return value!!
    }
}
