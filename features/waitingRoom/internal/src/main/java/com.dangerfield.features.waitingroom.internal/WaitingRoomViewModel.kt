package com.dangerfield.features.waitingroom.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.waitingroom.accessCodeArgument
import com.dangerfield.features.waitingroom.videoCallLinkArgument
import com.dangerfield.libraries.navigation.navArgument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val actions = Channel<Action>(Channel.UNLIMITED)
    private val _events = Channel<Event>()
    val events = _events.receiveAsFlow()

    val state = flow {

        val accessCode = savedStateHandle.navArgument<String>(accessCodeArgument)
        val videoCallLink = savedStateHandle.navArgument<String>(videoCallLinkArgument)

        emit(
            State(
                accessCode = accessCode.orEmpty(),
                players = listOf("Josiah", "Arif", "Michael", "Eli", "Nibraas", "George"),
                isLoadingRoom = false,
                isLoadingStart = false,
                videoCallLink = videoCallLink,
                didSomethingGoWrong = accessCode.isNullOrBlank()
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = State(
            accessCode = "",
            players = emptyList(),
            isLoadingRoom = true,
            isLoadingStart = false,
            videoCallLink = null
        )
    )

    data class State(
        val accessCode: String,
        val players: List<String>,
        val isLoadingRoom: Boolean,
        val isLoadingStart: Boolean,
        val didSomethingGoWrong: Boolean = false,
        val videoCallLink: String?
    )

    sealed class Action {

    }

    sealed class Event {

    }
}