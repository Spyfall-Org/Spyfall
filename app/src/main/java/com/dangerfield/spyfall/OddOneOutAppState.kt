package com.dangerfield.spyfall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dangerfield.libraries.network.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import oddoneout.core.AppState

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): OddOneOutAppState {
    return remember(
        coroutineScope,
        networkMonitor,
    ) {
        OddOneOutAppState(
            networkMonitor,
            coroutineScope,
        )
    }
}

@Stable
class OddOneOutAppState(
    networkMonitor: NetworkMonitor,
    val coroutineScope: CoroutineScope,
): AppState {

    override val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
