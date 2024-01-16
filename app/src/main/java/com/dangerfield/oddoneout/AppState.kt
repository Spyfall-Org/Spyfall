package com.dangerfield.oddoneout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dangerfield.libraries.network.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AppState {
    return remember(
        coroutineScope,
        networkMonitor,
    ) {
        AppState(
            networkMonitor,
            coroutineScope,
        )
    }
}

@Stable
class AppState(
    networkMonitor: NetworkMonitor,
    val coroutineScope: CoroutineScope,
) {

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
