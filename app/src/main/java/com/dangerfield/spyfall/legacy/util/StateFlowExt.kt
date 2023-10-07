package com.dangerfield.spyfall.legacy.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.collectWhileStarted(lifecycleOwner: LifecycleOwner, block: (T) -> Unit) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            collectLatest {
                block(it)
            }
        }
    }
}


/**
 * utility function to being collecting a flow when the base LifecycleOwner
 * reaches the started state and stop when it reaches the stopped state
 *
 * catches and logs errors by default
 */
inline fun <T> LifecycleOwner.collectWhileStarted(
    flow: Flow<T>,
    crossinline onError: (Throwable) -> Unit = {},
    crossinline onSuccess: suspend (T) -> Unit
): Job =
    lifecycleScope.launch {
        flow.flowWithLifecycle(lifecycle)
            .catch { onError(it) }
            .collectLatest { onSuccess(it) }
    }
