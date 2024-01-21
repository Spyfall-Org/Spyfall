package com.dangerfield.features.gameplay.internal.singledevice.rolereveal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.singledevice.SingleDeviceGameMetricTracker
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Event
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.FieldState.Invalid
import com.dangerfield.oddoneoout.features.gameplay.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import oddoneout.core.developerSnackIfDebug
import oddoneout.core.doNothing
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SingleDeviceRoleRevealViewModel @Inject constructor(
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val mapToGameState: MapToGameStateUseCase,
    private val clearActiveGame: ClearActiveGame,
    private val gameConfig: GameConfig,
    private val dictionary: Dictionary,
    private val singleDeviceGameMetricTracker: SingleDeviceGameMetricTracker
) : SEAViewModel<State, Event, Action>() {

    private val playersToShowRoles: MutableSet<DisplayablePlayer> = LinkedHashSet()
    private var currentPlayerRoleIndex = 0

    private val gameFlow: SharedFlow<Game> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )


    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val hasLoadedGame = AtomicBoolean(false)

    override val initialState = State(
        currentPlayer = null,
        location = null,
        isGameStarted = false,
        isLastPlayer = false,
        nameFieldState = FieldState.Idle(""),
        locationOptions = emptyList(),
        isFirstPlayer = false
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadGame -> loadGame()
            Action.LoadNextPlayer -> {
                currentPlayerRoleIndex =
                    (currentPlayerRoleIndex + 1).coerceAtMost(playersToShowRoles.size - 1)
                loadPlayer(currentPlayerRoleIndex)
            }

            Action.StartGame -> startGame()
            is Action.UpdateName -> changeName(action.name)
            Action.LoadPreviousPlayer -> {
                currentPlayerRoleIndex = (currentPlayerRoleIndex - 1).coerceAtLeast(0)
                loadPlayer(currentPlayerRoleIndex)
            }

            Action.EndGame -> endGame()
        }
    }

    private suspend fun startGame() {
        gameRepository.start(accessCode)
            .onSuccess {
                singleDeviceGameMetricTracker.trackGameStarted(
                    game = getGame()
                )
            }
            .onFailure {
                singleDeviceGameMetricTracker.trackGameStartFailure(
                    game = getGame(),
                    throwable = it
                )
            }
    }

    private suspend fun changeName(newName: String) {
        val id = state.value.currentPlayer?.id ?: return

        val isNameInvalidLength =
            newName.length !in gameConfig.minNameLength..gameConfig.maxNameLength

        val fieldState = when {
            newName.isNotEmpty() && isNameInvalidLength -> Invalid(
                newName,
                dictionary.getString(
                    R.string.roleReveal_nameInvalidLengthError_text,
                    mapOf(
                        "min" to gameConfig.minNameLength.toString(),
                        "max" to gameConfig.maxNameLength.toString()
                    )
                )
            )

            gameFlow.first().players.find { it.userName.lowercase() == newName.lowercase() } != null -> Invalid(
                newName,
                dictionary.getString(R.string.roleReveal_nameTakenError_text)
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
                    loadPlayer(currentPlayerRoleIndex)
                }
        }

        viewModelScope.launch {
            gameFlow
                .map { game ->
                    mapToGameState(accessCode, game)
                }.collect { gameState ->
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

    private suspend fun endGame() {
        gameRepository.end(accessCode)
        // TODO pack game ending behind a uses case and leave game for that matter.
        // not super sure I need a data source as well as a repository
        // maybe I continue trucking on and clean that up later
        clearActiveGame()
        sendEvent(Event.GameKilled)
        singleDeviceGameMetricTracker.trackGameEnded(
            game = getGame(),
            timeRemainingMillis = 0
        )
    }

    private suspend fun getGame() = gameFlow.replayCache.firstOrNull() ?: gameFlow.first()

    private suspend fun loadPlayer(index: Int) {

        updateState { it.copy(nameFieldState = FieldState.Idle("")) }

        val player = playersToShowRoles.elementAtOrNull(index)

        if (player != null) {
            updateState {
                it.copy(
                    currentPlayer = player,
                    isLastPlayer = index == playersToShowRoles.size - 1,
                    isFirstPlayer = index == 0,
                    nameFieldState = FieldState.Idle(player.name)
                )
            }
        }
    }

    data class State(
        val currentPlayer: DisplayablePlayer?,
        val isLastPlayer: Boolean,
        val isFirstPlayer: Boolean,
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
        data object LoadPreviousPlayer : Action()
        data object StartGame : Action()
        data object EndGame : Action()
        data class UpdateName(val name: String) : Action()
    }
}