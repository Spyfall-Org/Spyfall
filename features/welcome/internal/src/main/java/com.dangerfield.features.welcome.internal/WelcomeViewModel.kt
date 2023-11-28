package com.dangerfield.features.welcome.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import com.dangerfield.libraries.coreflowroutines.launchOnStart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val isAppUpdateRequired: IsAppUpdateRequired
) : ViewModel() {

    private val uiEventsFlow = MutableStateFlow(setOf<WelcomeEvent>())

    val state = uiEventsFlow.map {
        State(
            events = it.toList()
        )
    }
        .launchOnStart {
            if (isAppUpdateRequired()) {
                uiEventsFlow.update {
                    it + WelcomeEvent.ForcedUpdateRequired
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = State(
                events = emptyList()
            )
        )

    fun onEventHandled(event: WelcomeEvent) {
        uiEventsFlow.update {
            it - event
        }
    }

    data class State(
        val events: List<WelcomeEvent>,
    )

    sealed class WelcomeEvent {
        data object ForcedUpdateRequired : WelcomeEvent()
    }
}
