package com.dangerfield.features.gameplay.internal.singledevice.rolereveal

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Event
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.FieldState.Invalid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import spyfallx.core.developerSnackIfDebug
import spyfallx.core.doNothing
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SingleDeviceRoleRevealViewModel @Inject constructor(
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val mapToGameState: MapToGameStateUseCase,
    private val clearActiveGame: ClearActiveGame,
    private val gameConfig: GameConfig
) : SEAViewModel<State, Event, Action>() {

    private val playersToShowRoles: MutableSet<DisplayablePlayer> = LinkedHashSet()

    private val gameFlow by lazy {
        gameRepository.getGameFlow(accessCode)
    }

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val hasLoadedGame = AtomicBoolean(false)

    override val initialState = State(
        currentPlayer = null,
        location = null,
        isGameStarted = false,
        isLastPlayer = false,
        nameFieldState = FieldState.Idle(""),
        locationOptions = emptyList()
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadGame -> loadGame()
            Action.LoadNextPlayer -> pollPlayer()
            Action.StartGame -> startGame()
            is Action.UpdateName -> changeName(action.name)
        }
    }

    private suspend fun startGame() {
        gameRepository.start(accessCode)
    }

    private suspend fun changeName(newName: String) {
        val id = state.value.currentPlayer?.id ?: return

        val isNameInvalidLength =
            newName.length !in gameConfig.minNameLength..gameConfig.maxNameLength

        val fieldState = when {
            newName.isNotEmpty() && isNameInvalidLength -> Invalid(
                newName,
                "Name must be between ${gameConfig.minNameLength} and ${gameConfig.maxNameLength} characters"
            )

            gameFlow.first().players.find { it.userName.lowercase() == newName.lowercase() } != null -> Invalid(
                newName,
                "Name is taken"
            )

            else -> FieldState.Valid(newName)
        }

        updateState {
            it.copy(nameFieldState = fieldState)
        }

        if (fieldState is FieldState.Valid) {
            gameRepository.changeName(
                accessCode = accessCode,
                newName = newName,
                id = id
            )
        }
    }

    private suspend fun loadGame() {
        if (hasLoadedGame.getAndSet(true)) return

        viewModelScope.launch {
            gameRepository.getGame(accessCode)
                .onSuccess { game ->
                    updateState { state ->
                        state.copy(
                            location = game.locationName,
                            locationOptions = game.locationOptionNames
                        )
                    }
                    val displayablePlayers = game.players.mapIndexed { index, player ->
                        DisplayablePlayer(
                            name = player.userName,
                            id = player.id,
                            role = player.role ?: "",
                            isFirst = index == 0,
                            isOddOneOut = player.isOddOneOut
                        )
                    }
                    playersToShowRoles.clear()
                    playersToShowRoles.addAll(displayablePlayers)
                    pollPlayer()
                }
        }

        viewModelScope.launch {
            gameFlow
                .map { game ->
                    mapToGameState(accessCode, game)
                }.collect { gameState ->
                    Log.d("Elijah", "Game state of ${gameState::class.simpleName} in role reveal")

                    when (gameState) {
                        is GameState.Expired,
                        is GameState.Voting,
                        is GameState.VotingEnded,
                        is GameState.Unknown -> developerSnackIfDebug {
                            "Illegal game state ${gameState::class.java.simpleName}"
                        }

                        is GameState.Starting,
                        is GameState.Waiting
                        -> doNothing()

                        is GameState.DoesNotExist -> {
                            clearActiveGame()
                            sendEvent(Event.GameKilled)
                        }

                        is GameState.Started -> {
                            // will end up here
                            // send an event? nah probs just update the state and use launched effect
                            updateState {
                                it.copy(
                                    isGameStarted = true
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun pollPlayer() {
        updateState { it.copy(nameFieldState = FieldState.Idle("")) }

        val nextPlayer = playersToShowRoles.elementAtOrNull(0)

        val currentPlayer = nextPlayer?.let {
            playersToShowRoles.remove(it)
            it
        }

        if (currentPlayer != null) {
            updateState {
                it.copy(
                    currentPlayer = currentPlayer,
                    isLastPlayer = playersToShowRoles.isEmpty()
                )
            }
        }
    }

    data class State(
        val currentPlayer: DisplayablePlayer?,
        val isLastPlayer: Boolean,
        val location: String?,
        val isGameStarted: Boolean,
        val nameFieldState: FieldState<String>,
        val locationOptions: List<String>,
    )

    sealed class Event {
        data object GameKilled : Event()
    }

    sealed class Action {
        internal data object LoadGame : Action()
        data object LoadNextPlayer : Action()
        data object StartGame : Action()
        data class UpdateName(val name: String) : Action()
    }
}