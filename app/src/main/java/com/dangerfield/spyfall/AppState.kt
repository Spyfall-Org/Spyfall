package com.dangerfield.spyfall

import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope

@Stable
class AppState(
    private val coroutineScope: CoroutineScope
) {

    // TODO pick this back up
//    val isOffline = networkMonitor.isOnline
//        .map(Boolean::not)
//        .stateIn(
//            scope = coroutineScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = false,
//        )
}