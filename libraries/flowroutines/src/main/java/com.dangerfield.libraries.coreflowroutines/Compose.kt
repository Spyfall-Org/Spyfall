package com.dangerfield.libraries.coreflowroutines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

//TODO document
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

suspend fun <T> Flow<T>.observeWithLifecycle(lifecycleOwner: Lifecycle, onItem: suspend (T) -> Unit) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        withContext(Dispatchers.Main.immediate) {
            collect(onItem)
        }
    }
}