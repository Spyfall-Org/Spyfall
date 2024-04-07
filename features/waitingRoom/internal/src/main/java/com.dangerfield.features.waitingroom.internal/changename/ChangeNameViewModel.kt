package com.dangerfield.features.waitingroom.internal.changename

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.waitingroom.internal.changename.ChangeNameViewModel.Action
import com.dangerfield.features.waitingroom.internal.changename.ChangeNameViewModel.Event
import com.dangerfield.features.waitingroom.internal.changename.ChangeNameViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.shareIn
import oddoneout.core.allOrNone
import oddoneout.core.eitherWay
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChangeNameViewModel @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val gameConfig: GameConfig,
    private val session: Session
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    private val accessCode: String
        get() = savedStateHandle.navArgument(changeNameAccessCodeArgument)
            ?: session.activeGame?.accessCode
            ?: ""

    private val gameFlow: SharedFlow<Game?> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    override fun initialState() = State(
        name = "",
        isNameTaken = false,
        isNameInvalidLength = false,
        didSomethingGoWrong = false,
        minNameLength = gameConfig.minNameLength,
        maxNameLength = gameConfig.maxNameLength,
        isLoading = false
    )

    override suspend fun handleAction(action: Action) {
        with(action) {
            when (this) {
                is Action.UpdateName -> updateName()
                is Action.SubmitNameChange -> handleNameChangeSubmit()
            }
        }
    }

    private suspend fun Action.SubmitNameChange.handleNameChangeSubmit() {
        val game = getGame()

        if (game == null) {
            updateState { it.copy(didSomethingGoWrong = true) }
            return
        }

        allOrNone(
            accessCode,
            session.activeGame?.userId
        ) { accessCode, userId ->
            val allPlayers = game.players
            val mePlayer = allPlayers.firstOrNull { it.id == userId }
            val notMePlayers = allPlayers.filter { it != mePlayer }
            val isNameTaken = notMePlayers.any { it.userName == name }
            val isAlreadyMyName = mePlayer?.userName == name
            val nameIsInvalidLength =
                name.length !in gameConfig.minNameLength..gameConfig.maxNameLength

            when {
                isAlreadyMyName -> sendEvent(Event.NameChanged)
                nameIsInvalidLength -> updateState { it.copy(isNameInvalidLength = true) }
                isNameTaken -> updateState { it.copy(isNameTaken = true) }
                else -> submitNameChange(
                    userId = userId,
                    accessCode = accessCode,
                )
            }
        } ?: updateState { it.copy(didSomethingGoWrong = true) }
    }

    private suspend fun Action.SubmitNameChange.submitNameChange(
        userId: String,
        accessCode: String,
    ) {
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
    private suspend fun Action.UpdateName.updateName(
    ) {
        updateState { it.copy(name = name) } // prevent lag

        val game = getGame()

        if (game == null) {
            updateState { it.copy(didSomethingGoWrong = true) }
            return
        }

        val players = game.players

        val notMePlayers = players.filter { it.id != session.activeGame?.userId }

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

    private suspend fun getGame() =
        gameFlow.replayCache.firstOrNull()
            ?: gameFlow.filterNotNull().firstOrNull()
            ?: gameRepository.getGame(accessCode).getOrNull()

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