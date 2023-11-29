package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LazySuspend<T>(private val initializer: suspend () -> T) {
    private var value: T? = null
    private val mutex = Mutex()

    suspend fun getValue(): T {
        if (value == null) {
            mutex.withLock {
                if (value == null) { // double check inside lock
                    value = initializer()
                }
            }
        }
        return value!!
    }
}
