package com.dangerfield.spyfall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.spyfall.legacy.ui.forcedupdate.IsUpdateRequired
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import spyfallx.core.BuildInfo

class MainActivityViewModel(
    private val isUpdateRequired: IsUpdateRequired,
    private val buildInfo: BuildInfo
) : ViewModel() {

    val state: StateFlow<State> = flow {
        val shouldRequireUpdate = buildInfo.isLegacySpyfall && isUpdateRequired()
        emit(
            if (shouldRequireUpdate) State.UpdateRequired else State.Idle
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = State.Loading
        )

    sealed class State {
        data object Idle: State()
        data object Loading: State()
        data object UpdateRequired: State()
    }
}
