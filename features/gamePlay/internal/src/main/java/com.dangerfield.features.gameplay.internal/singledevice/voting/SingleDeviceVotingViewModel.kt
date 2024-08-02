package com.dangerfield.features.gameplay.internal.singledevice.voting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.singledevice.SingleDeviceGameMetricTracker
import com.dangerfield.features.gameplay.internal.singledevice.voting.SingleDeviceVotingViewModel.Action
import com.dangerfield.features.gameplay.internal.singledevice.voting.SingleDeviceVotingViewModel.Event
import com.dangerfield.features.gameplay.internal.singledevice.voting.SingleDeviceVotingViewModel.State
import com.dangerfield.features.gameplay.internal.toDisplayable
import com.dangerfield.libraries.coreflowroutines.LazySuspend
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
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
class SingleDeviceVotingViewModel @Inject constructor(
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val clearActiveGame: ClearActiveGame,
    private val singleDeviceGameMetricTracker: SingleDeviceGameMetricTracker
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    private var currentPlayerRoleIndex = 0
    private val playersToShowRoles: MutableSet<DisplayablePlayer> = LinkedHashSet()

    private val gameFlow: SharedFlow<Game?> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    private val allPlayers = LazySuspend {
        getGame()?.players?.mapIndexed { index, player ->
            player.toDisplayable(index == 0)
        } ?: emptyList<DisplayablePlayer>().also {
            Timber.e("No players found for game")
            showDebugSnack { "No players found for game" }
        }
    }

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val hasLoadedGame = AtomicBoolean(false)
    private val hasRecordedResult = AtomicBoolean(false)

    override fun initialState() = State(
        currentPlayer = null,
        isLastPlayer = false,
        locationOptions = emptyList(),
        playerOptions = emptyList(),
        isLoadingResult = false,
        location = "",
        oddOneOutName = "",
        correctGuessesForOddOneOut = 0,
        oddOneOutLocationGuess = null,
        result = GameResult.None,
        isFirstPlayer = false,
        totalPlayerCount = 0,
    )

    override suspend fun handleAction(action: Action) {
        with(action) {
            when (action) {
                is Action.LoadGame -> action.loadGame()
                is Action.EndGame -> action.endGame()
                is Action.ResetGame -> action.resetGame()
                is Action.SubmitVoteForLocation -> action.submitVoteForLocation()
                is Action.SubmitVoteForPlayer -> action.submitVoteForPlayer()
                is Action.WaitForResults -> {
                    gameRepository.refreshState()
                    updateState { state -> state.copy(isLoadingResult = true) }
                }
                is Action.PreviousPlayer -> loadPlayer(--currentPlayerRoleIndex)
            }
        }
    }

    private suspend fun Action.SubmitVoteForLocation.submitVoteForLocation(
    ) {
        loadPlayer(++currentPlayerRoleIndex)
        gameRepository.submitVoteForSecret(
            accessCode = accessCode,
            voterId = voterId,
            secret = location,
        )

        updateState { state -> state.copy(oddOneOutLocationGuess = location) }
    }

    private suspend fun Action.SubmitVoteForPlayer.submitVoteForPlayer() {
        loadPlayer(++currentPlayerRoleIndex)
        gameRepository.submitVoteForOddOneOut(
            accessCode = accessCode,
            voterId = voterId,
            voteId = playerId
        ).getOrElse { false }
    }

    private fun Action.LoadGame.loadGame(
    ) {
        if (hasLoadedGame.getAndSet(true)) return
        setInitialGameState()
        listenForGameUpdates()
    }

    private fun Action.LoadGame.setInitialGameState(
    ) {
        viewModelScope.launch {
            gameRepository.getGame(accessCode)
                .onSuccess { game ->
                    val displayablePlayers = game.players.mapIndexed { index, player ->
                        player.toDisplayable(index == 0)
                    }

                    val playersToVote = game.players.filter { !it.hasVoted() }

                    updateState { state ->
                        state.copy(
                            locationOptions = game.secretOptions,
                            playerOptions = displayablePlayers,
                            location = game.secretItem.name,
                            oddOneOutName = displayablePlayers.first { it.isOddOneOut }.name,
                        )
                    }

                    playersToShowRoles.clear()
                    playersToShowRoles.addAll(playersToVote.map { it.toDisplayable(false) })
                    currentPlayerRoleIndex = 0
                    loadPlayer(currentPlayerRoleIndex)
                }
        }
    }

    private fun Action.LoadGame.listenForGameUpdates(
    ) {
        viewModelScope.launch {
            gameFlow
                .map { game ->
                    when (game?.state) {
                        Game.State.Expired,
                        Game.State.Started,
                        Game.State.Starting,
                        Game.State.Unknown -> showDebugSnack {
                            "Illegal game state with game $game"
                        }

                        Game.State.Waiting -> {
                            sendEvent(Event.GameReset)
                        }

                        null -> {
                            clearActiveGame()
                            sendEvent(Event.GameKilled)
                        }

                        Game.State.Voting -> doNothing()
                        Game.State.Results -> {
                            recordResult(game)

                            updateState { state ->
                                state.copy(
                                    correctGuessesForOddOneOut = game.players.filter { !it.isOddOneOut }
                                        .filter { it.votedCorrectly() }.size,
                                    totalPlayerCount = game.players.size,
                                    location = game.secretItem.name,
                                    oddOneOutName = game.players.first { it.isOddOneOut }.userName,
                                    result = game.result,
                                    isLoadingResult = false
                                )
                            }
                        }
                    }
                }.collect()
        }
    }

    private fun recordResult(
        game: Game
    ) {
        if (!hasRecordedResult.getAndSet(true)) {
            singleDeviceGameMetricTracker.trackVotingEnded(
                game = game
            )
        }
    }

    private suspend fun Action.loadPlayer(index: Int) {
        val allPlayers = allPlayers.getValue()
        val player = playersToShowRoles.elementAtOrNull(index)

        if (player != null) {
            updateState {
                it.copy(
                    currentPlayer = player,
                    isLastPlayer = index == playersToShowRoles.size - 1,
                    isFirstPlayer = index == 0,
                    playerOptions = allPlayers.filter { it.id != player.id }
                )
            }
        }
    }

    private suspend fun Action.EndGame.endGame() {
        gameRepository.end(accessCode)
        clearActiveGame()
        sendEvent(Event.GameKilled)
        singleDeviceGameMetricTracker.trackGameEnded(
            game = getGame(),
            timeRemainingMillis = 0
        )
    }

    private suspend fun Action.ResetGame.resetGame() {
        gameRepository.reset(accessCode)
            .onSuccess {
                singleDeviceGameMetricTracker.trackGameRestarted(
                    game = getGame(),
                    timeRemainingMillis = 0
                )
            }.onFailure {
                singleDeviceGameMetricTracker.trackGameRestartError(
                    game = getGame(),
                    timeRemainingMillis = 0,
                    error = it
                )

                sendEvent(Event.ResetFailed)
            }
    }

    private suspend fun getGame() =
        gameFlow.replayCache.firstOrNull()
            ?: gameFlow.filterNotNull().firstOrNull()
            ?: gameRepository.getGame(accessCode).getOrNull()

    data class State(
        val currentPlayer: DisplayablePlayer?,
        val isLastPlayer: Boolean,
        val isFirstPlayer: Boolean,
        val totalPlayerCount: Int,
        val locationOptions: List<String>,
        val isLoadingResult: Boolean,
        val playerOptions: List<DisplayablePlayer>,
        val location: String,
        val oddOneOutName: String,
        val correctGuessesForOddOneOut: Int,
        val oddOneOutLocationGuess: String?,
        val result: GameResult = GameResult.None,
    )

    sealed class Event {
        data object GameKilled : Event()
        data object GameReset : Event()
        data object ResetFailed : Event()
    }

    sealed class Action {
        object LoadGame : Action()
        data class SubmitVoteForPlayer(val voterId: String, val playerId: String) : Action()
        data object WaitForResults : Action()
        data class SubmitVoteForLocation(val voterId: String, val location: String) : Action()
        data object PreviousPlayer : Action()
        data object EndGame : Action()
        data object ResetGame : Action()
    }
}