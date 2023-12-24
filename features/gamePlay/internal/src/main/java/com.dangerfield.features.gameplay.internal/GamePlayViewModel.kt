package com.dangerfield.features.gameplay.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Event
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.State
import com.dangerfield.features.gameplay.timeLimitArgument
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.TriggerFlow
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import spyfallx.core.developerSnackIfDebug
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class GamePlayViewModel @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    private val session: Session,
    private val clearActiveGame: ClearActiveGame,
    private val savedStateHandle: SavedStateHandle
) : SEAViewModel<State, Event, Action>() {

    private val meUserId = session.activeGame?.userId ?: ""
    private val isSubscribedToGameFlow = AtomicBoolean(false)

    // TODO may be concurency issues here, the timer could be stoped or started from different coroutines
    private var timerJob: Job? = null
    private val gameTimeRefreshTrigger = TriggerFlow()
    private val isTimerRunning: Boolean get() = timerJob != null

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val timeLimitArg: Int?
        get() = savedStateHandle.navArgument<Int>(timeLimitArgument).takeIf { (it ?: 0) > 0 }

    override val initialState = State(
        players = emptyList(),
        locations = emptyList(),
        timeRemaining = timeLimitArg?.minutes?.inWholeMilliseconds?.millisToMMss().orEmpty(),
        isLoadingLocations = true,
        isLoadingPlayers = true,
        didSomethingGoWrongLoading = accessCode.isEmpty() || meUserId.isEmpty(),
        didSomethingGoWrongVoting = false,
        isTimeUp = false,
        mePlayer = null,
        location = null,
        isLoadingVoteSubmit = false,
        isVoteSubmitted = false,
        gameResult = null,
        videoCallLink = null
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadGamePlay -> loadGamePlay()
            is Action.SubmitLocationVote -> submitLocationVote(action)
            is Action.SubmitOddOneOutVote -> submitOddOneOutVote(action)
            Action.ResetGame -> resetGame()
            Action.EndGame -> endGame()
        }
    }

    private suspend fun endGame() {
        gameRepository.end(accessCode)
        // TODO pack game ending behind a uses case and leave game for that matter.
        // not super sure I need a data source as well as a repository
        // maybe I continue trucking on and clean that up later

        clearActiveGame()
        sendEvent(Event.GameKilled)
    }

    private suspend fun resetGame() {
        gameRepository.reset(accessCode)
    }

    private suspend fun submitOddOneOutVote(action: Action.SubmitOddOneOutVote) {
        updateState { it.copy(isLoadingVoteSubmit = true) }
        gameRepository.submitOddOneOutVote(
            accessCode,
            voterId = meUserId,
            voteId = action.id
        )
            .onSuccess {
                updateState { it.copy(isVoteSubmitted = true) }
            }
            .onFailure {
                updateState { it.copy(didSomethingGoWrongVoting = true) }
            }
            .eitherWay {
                updateState { it.copy(isLoadingVoteSubmit = false) }
            }
    }

    private suspend fun submitLocationVote(action: Action.SubmitLocationVote) {
        updateState { it.copy(isLoadingVoteSubmit = true) }
        gameRepository.submitLocationVote(
            accessCode,
            voterId = meUserId,
            location = action.location
        )
            .onSuccess {
                updateState { it.copy(isVoteSubmitted = true) }
            }
            .onFailure {
                updateState { it.copy(didSomethingGoWrongVoting = true) }
            }
            .eitherWay {
                updateState { it.copy(isLoadingVoteSubmit = false) }
            }
    }

    private suspend fun loadGamePlay() {
        if (isSubscribedToGameFlow.getAndSet(true)) return
        viewModelScope.launch {
            combine(
                gameRepository.getGameFlow(accessCode),
                gameTimeRefreshTrigger
            ) { game, _ ->
                mapToGameState(accessCode, game)
            }.collect { gameState ->
                when (gameState) {
                    is GameState.Starting,
                    is GameState.Expired,
                    is GameState.Unknown -> developerSnackIfDebug {
                        "Illegal game state ${gameState::class.java.simpleName}"
                    }

                    is GameState.DoesNotExist -> {
                        clearActiveGame()
                        sendEvent(Event.GameKilled)
                    }

                    is GameState.Waiting -> sendEvent(Event.GameReset(accessCode))
                    is GameState.Started -> updateInProgressGame(gameState)
                    is GameState.Voting -> updateVotingGame(gameState)
                    is GameState.VotingEnded -> updateVotingEndedGame(gameState)
                }
            }
        }
    }

    private suspend fun updateVotingEndedGame(gameState: GameState.VotingEnded) {
        stopTimer()
        updateState { prev ->
            prev.copy(
                isLoadingPlayers = false,
                isLoadingLocations = false,
                videoCallLink = gameState.videoCallLink,
                players = gameState.players.map { player ->
                    DisplayablePlayer(
                        name = player.userName,
                        isFirst = false,
                        id = player.id,
                        role = player.role ?: "",
                        isOddOneOut = player.isOddOneOut
                    )
                },
                mePlayer = gameState.players.find { it.id == meUserId }
                    ?.let { me ->
                        DisplayablePlayer(
                            name = me.userName,
                            isFirst = false,
                            id = me.id,
                            role = me.role ?: "",
                            isOddOneOut = me.isOddOneOut
                        )
                    },
                locations = gameState.locationNames,
                location = gameState.location,
                isTimeUp = true,
                gameResult = gameState.result,
            )
        }
    }

    private suspend fun updateVotingGame(gameState: GameState.Voting) {
        stopTimer()
        updateState { prev ->
            prev.copy(
                isLoadingPlayers = false,
                isLoadingLocations = false,
                videoCallLink = gameState.videoCallLink,
                players = gameState.players.map { player ->
                    DisplayablePlayer(
                        name = player.userName,
                        isFirst = false,
                        id = player.id,
                        role = player.role ?: "",
                        isOddOneOut = player.isOddOneOut
                    )
                },
                mePlayer = gameState.players.find { it.id == meUserId }
                    ?.let { me ->
                        DisplayablePlayer(
                            name = me.userName,
                            isFirst = false,
                            id = me.id,
                            role = me.role ?: "",
                            isOddOneOut = me.isOddOneOut
                        )
                    },
                locations = gameState.locationNames,
                location = gameState.location,
                isTimeUp = true

            )
        }
    }

    private suspend fun updateInProgressGame(gameState: GameState.Started) {
        if (!isTimerRunning) startTimer()

        updateState { prev ->
            prev.copy(
                isLoadingPlayers = false,
                isLoadingLocations = false,
                videoCallLink = gameState.videoCallLink,
                players = gameState.players.map { player ->
                    DisplayablePlayer(
                        name = player.userName,
                        isFirst = gameState.firstPlayer == player,
                        id = player.id,
                        role = player.role ?: "",
                        isOddOneOut = player.isOddOneOut
                    )
                },
                mePlayer = gameState.players.find { it.id == meUserId }
                    ?.let { me ->
                        DisplayablePlayer(
                            name = me.userName,
                            isFirst = gameState.firstPlayer == me,
                            id = me.id,
                            role = me.role ?: "",
                            isOddOneOut = me.isOddOneOut
                        )
                    },
                locations = gameState.locationNames,
                location = gameState.location,
                timeRemaining = gameState.timeRemainingMillis.millisToMMss(),
            )
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startTimer() {
        if (isTimerRunning) return
        timerJob = viewModelScope.launch {
            while (isActive) {
                gameTimeRefreshTrigger.pull()
                delay(500)
            }
        }
    }

    @Suppress("ImplicitDefaultLocale")
    private fun Long.millisToMMss(): String {
        val minutes = this / 60000
        val seconds = (this % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    data class State(
        val isLoadingPlayers: Boolean,
        val isLoadingLocations: Boolean,
        val isLoadingVoteSubmit: Boolean,
        val isVoteSubmitted: Boolean,
        val players: List<DisplayablePlayer>,
        val isTimeUp: Boolean,
        val mePlayer: DisplayablePlayer?,
        val locations: List<String>,
        val location: String?,
        val timeRemaining: String,
        val didSomethingGoWrongLoading: Boolean,
        val didSomethingGoWrongVoting: Boolean,
        val videoCallLink: String?,
        val gameResult: GameResult?,
    )

    sealed class Event {
        data class GameReset(val accessCode: String) : Event()
        data object GameKilled : Event()
    }

    sealed class Action {
        data object ResetGame : Action()
        data object LoadGamePlay : Action()
        data object EndGame : Action()
        data class SubmitOddOneOutVote(val id: String) : Action()
        data class SubmitLocationVote(val location: String) : Action()
    }
}