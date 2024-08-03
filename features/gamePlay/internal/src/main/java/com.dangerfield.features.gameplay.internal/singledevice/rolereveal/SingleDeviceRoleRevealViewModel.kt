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
import com.dangerfield.libraries.coreflowroutines.collectIn
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.getString
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.FieldState.Invalid
import com.dangerfield.oddoneoout.features.gameplay.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import oddoneout.core.showDebugSnack
import oddoneout.core.doNothing
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SingleDeviceRoleRevealViewModel @Inject constructor(
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val clearActiveGame: ClearActiveGame,
    private val gameConfig: GameConfig,
    private val dictionary: Dictionary,
    private val packRepository: PackRepository,
    private val singleDeviceGameMetricTracker: SingleDeviceGameMetricTracker
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    private val playersToShowRoles: MutableSet<DisplayablePlayer> = LinkedHashSet()
    private var currentPlayerRoleIndex = 0

    private val gameFlow: SharedFlow<Game?> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val hasLoadedGame = AtomicBoolean(false)

    override fun initialState() = State(
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
            is Action.LoadGame -> action.loadGame()
            is Action.LoadNextPlayer -> {
                currentPlayerRoleIndex =
                    (currentPlayerRoleIndex + 1).coerceAtMost(playersToShowRoles.size - 1)
                loadPlayer(currentPlayerRoleIndex) { action.updateState(it) }
            }

            is Action.StartGame -> startGame()
            is Action.UpdateName -> action.changeName()
            is Action.LoadPreviousPlayer -> {
                currentPlayerRoleIndex = (currentPlayerRoleIndex - 1).coerceAtLeast(0)
                loadPlayer(currentPlayerRoleIndex) { action.updateState(it) }
            }

            is Action.EndGame -> endGame()
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

    private suspend fun Action.UpdateName.changeName() {
        val id = state.currentPlayer?.id ?: return
        val newName = this.name
        val game = getGame()

        if (game == null) {
            Timber.e("Game is null when changing name")
            return
        }

        val isNameInvalidLength =
            newName.length !in gameConfig.minNameLength..gameConfig.maxNameLength

        val fieldState = when {
            newName.isNotEmpty() && isNameInvalidLength -> Invalid(
                newName,
                dictionary.getString(
                    R.string.roleReveal_nameInvalidLengthError_text,
                    "min" to gameConfig.minNameLength.toString(),
                    "max" to gameConfig.maxNameLength.toString()
                )
            )

            game.players.find { it.userName.lowercase() == newName.lowercase() } != null -> Invalid(
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

    private suspend fun Action.LoadGame.loadGame() {
        if (hasLoadedGame.getAndSet(true)) return

        viewModelScope.launch {
            gameRepository.getGame(accessCode)
                .onSuccess { game ->

                    val packItem = packRepository.getPackItem(
                        itemName = game.secretItem.name,
                        version = game.packsVersion,
                        languageCode = game.languageCode
                    ).getOrNull()

                    updateState { state ->
                        state.copy(
                            location = packItem,
                            locationOptions = game.secretOptions
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
                    loadPlayer(currentPlayerRoleIndex) { updateState(it) }
                }
        }

        viewModelScope.launch {
            gameFlow
                .collectLatest { game ->
                    when (game?.state) {
                        Game.State.Expired,
                        Game.State.Voting,
                        Game.State.Results,
                        Game.State.Unknown -> showDebugSnack {
                            "Illegal game state with game $game"
                        }

                        Game.State.Starting,
                        Game.State.Waiting
                        -> doNothing()

                        null -> {
                            clearActiveGame()
                            sendEvent(Event.GameKilled)
                        }

                        is Game.State.Started -> {
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
        clearActiveGame()
        sendEvent(Event.GameKilled)
        singleDeviceGameMetricTracker.trackGameEnded(
            game = getGame(),
            timeRemainingMillis = 0
        )
    }

    private suspend fun getGame() =
        gameFlow.replayCache.firstOrNull()
            ?: gameFlow.filterNotNull().firstOrNull()
            ?: gameRepository.getGame(accessCode).getOrNull()

    private suspend fun loadPlayer(index: Int, update: suspend ((State) -> State) -> Unit) {
        update { it.copy(nameFieldState = FieldState.Idle("")) }

        val player = playersToShowRoles.elementAtOrNull(index)

        if (player != null) {
            update {
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
        val location: PackItem?,
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