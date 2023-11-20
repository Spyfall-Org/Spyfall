package com.dangerfield.spyfall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import spyfallx.coreui.color.ColorPrimitive
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isUpdateRequired: IsAppUpdateRequired,
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
