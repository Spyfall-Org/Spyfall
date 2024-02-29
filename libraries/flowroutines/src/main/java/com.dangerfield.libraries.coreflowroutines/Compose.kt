package com.dangerfield.libraries.coreflowroutines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Observe a flow with the lifecycle of the current composable
 * @param flow the flow to observe
 * @param onItem the action to take when an item is emitted from the flow
 *
 * Observes on main immediate which ensures no emissions are missed
 */
@Composable
fun <T> ObserveWithLifecycle(flow: Flow<T>, onItem: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner.lifecycle, flow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onItem)
            }
        }
    }
}

/**
 * Observe a flow with the provided lifecycle
 * @param onItem the action to take when an item is emitted from the flow
 *
 * starts collection when the lifecycle reaches the started state,
 * stops collection when the lifecycle falls below the started state,
 *
 * Observes on main immediate which ensures no emissions are missed
 */
suspend fun <T> Flow<T>.observeWithLifecycle(lifecycleOwner: Lifecycle, onItem: suspend (T) -> Unit) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        withContext(Dispatchers.Main.immediate) {
            collect(onItem)
        }
    }
}