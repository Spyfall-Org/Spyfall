package com.dangerfield.features.waitingroom.internal.changename

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.waitingroom.internal.changename.ChangeNameViewModel.*
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import spyfallx.core.allOrNone
import javax.inject.Inject

@HiltViewModel
class ChangeNameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val gameConfig: GameConfig,
    private val session: Session
) : SEAViewModel<State, Event, Action>() {

    private val accessCode: String?
        get() = savedStateHandle.navArgument(changeNameAccessCodeArgument)

    private val currentGamePlayers = gameRepository
        .getGameFlow(accessCode ?: "")
        .map { game -> game.players }
        .shareIn(
            scope = viewModelScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    override val initialState = State(
        name = "",
        isNameTaken = false,
        isNameInvalidLength = false,
        didSomethingGoWrong = false,
        minNameLength = gameConfig.minNameLength,
        maxNameLength = gameConfig.maxNameLength,
        isLoading = false
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            is Action.SubmitNameChange -> onNameChangeSubmitted(action)
            is Action.UpdateName -> updateName(action.name)
        }
    }

    private suspend fun allPlayers() = currentGamePlayers.replayCache.firstOrNull()
        ?: currentGamePlayers.first()

    private suspend fun onNameChangeSubmitted(
        action: Action.SubmitNameChange
    ) {
        allOrNone(
            accessCode,
            session.activeGame?.userId
        ) { accessCode, userId ->
            val allPlayers = allPlayers()
            val mePlayer = allPlayers.firstOrNull { it.id == userId }
            val notMePlayers = allPlayers.filter { it != mePlayer }
            val isNameTaken = notMePlayers.any { it.userName == action.name }
            val isAlreadyMyName = mePlayer?.userName == action.name
            val nameIsInvalidLength =
                action.name.length !in gameConfig.minNameLength..gameConfig.maxNameLength

            when {
                isAlreadyMyName -> sendEvent(Event.NameChanged)
                nameIsInvalidLength -> updateState { it.copy(isNameInvalidLength = true) }
                isNameTaken -> updateState { it.copy(isNameTaken = true) }
                else -> submitNameChange(
                    name = action.name,
                    userId = userId,
                    accessCode = accessCode
                )
            }
        } ?: updateState { it.copy(didSomethingGoWrong = true) }
    }

    private suspend fun submitNameChange(name: String, userId: String, accessCode: String) {
        updateState { it.copy(isLoading = true) }

        gameRepository.changeName(
            accessCode = accessCode,
            newName = name,
            id = userId
        )
            .onSuccess {
                sendEvent(Event.NameChanged)
            }
            .onFailure {
                updateState { it.copy(didSomethingGoWrong = true) }
            }
            .eitherWay {
                updateState { it.copy(isLoading = false) }
            }
    }

    // TODO would be ideal to debounce this, not hiding errors while typing
    private suspend fun updateName(name: String) {
        updateState { it.copy(name = name) } // prevent lag

        val notMePlayers =
            allPlayers().filter { it.id != session.activeGame?.userId }

        val isNameTaken = notMePlayers.any { it.userName == name }

        val nameIsInvalidLength = name.length !in gameConfig.minNameLength..gameConfig.maxNameLength

        updateState {
            it.copy(
                name = name,
                isNameTaken = isNameTaken,
                isNameInvalidLength = nameIsInvalidLength,
                didSomethingGoWrong = false,
            )
        }
    }

    sealed class Event {
        data object NameChanged : Event()
    }

    sealed class Action {
        data class UpdateName(val name: String) : Action()
        data class SubmitNameChange(val name: String) : Action()
    }

    data class State(
        val name: String,
        val isNameTaken: Boolean,
        val isNameInvalidLength: Boolean,
        val didSomethingGoWrong: Boolean,
        val minNameLength: Int,
        val maxNameLength: Int,
        val isLoading: Boolean
    )
}