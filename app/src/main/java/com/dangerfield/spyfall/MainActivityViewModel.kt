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

    /*
    okay so where do I get this color settings? core something? just in main
    no one needs it except main right?
    it should probably be something others can access

    on coreUI? cause its UI releated?
    coreUser, which exposes a use case to coreUser objects?
    colorPreference being one of those?

    should it be specific to color?
    coreColor?
    does it make sense in coreUi?
     */
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
