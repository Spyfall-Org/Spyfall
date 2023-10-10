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
import spyfallx.coreui.color.ColorPrimitive

class MainActivityViewModel(
    private val isUpdateRequired: IsUpdateRequired,
) : ViewModel() {

    val state: StateFlow<State> = flow {
        emit(
            State.Loaded(
                isUpdateRequired = isUpdateRequired(),
                accentColor = ColorPrimitive.CherryPop700
            )
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = State.Loading
        )

    sealed class State {
        data class Loaded(
            val isUpdateRequired: Boolean,
            val accentColor: ColorPrimitive
        ): State()
        data object Loading: State()
    }
}
