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
import com.dangerfield.libraries.navigation.navArgument
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
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class GamePlayViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    private val session: Session,
    private val savedStateHandle: SavedStateHandle
) : SEAViewModel<State, Event, Action>() {

    private val meUserId = session.activeGame?.userId ?: ""
    private val isSubscribedToGameFlow = AtomicBoolean(false)

    // TODO may be concurency issues here, the timer could be stoped or started from different coroutines
    private var timerJob: Job? = null
    private val isTimerRunning: Boolean get() = timerJob != null
    private val accessCode: String get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""
    private val timeLimitArg: Int? get() = savedStateHandle.navArgument(timeLimitArgument)
    private val gameTimeRefreshTrigger = TriggerFlow()

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
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadGamePlay -> loadGamePlay()
            is Action.SubmitLocationVote -> submitLocationVote(action)
            is Action.SubmitOddOneOutVote -> submitOddOneOutVote(action)
        }
    }

    private suspend fun GamePlayViewModel.submitOddOneOutVote(action: Action.SubmitOddOneOutVote) {
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

    private suspend fun GamePlayViewModel.submitLocationVote(action: Action.SubmitLocationVote) {
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
                    is GameState.DoesNotExist, is GameState.Starting, is GameState.Unknown -> developerSnackIfDebug {
                        "Illegal game state ${gameState::class.java.simpleName}"
                    }

                    is GameState.Waiting -> sendEvent(Event.GameReset)
                    is GameState.Started -> {
                        if (!isTimerRunning) startTimer()
                        updateState { prev ->
                            prev.copy(
                                isLoadingPlayers = false,
                                isLoadingLocations = false,
                                players = gameState.players.map { player ->
                                    DisplayablePlayer(
                                        name = player.userName,
                                        isFirst = gameState.firstPlayer == player,
                                        id = player.id,
                                        role = player.role ?: "",
                                        isOddOneOut = player.isOddOneOut
                                    )
                                },
                                mePlayer = gameState.players.find { it.id == session.user.id }
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

                    is GameState.Voting -> {
                        sendEvent(Event.GameTimedOut)
                        updateState { it.copy(isTimeUp = true) }
                        stopTimer()
                    }

                    is GameState.VotingEnded -> {
                        updateState {
                            it.copy(
                                gameResult = gameState.result,
                            )
                        }
                    }
                }
            }
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
        val gameResult: GameResult?,
    )

    sealed class Event {
        data object GameReset : Event()
        data object GameEnded : Event()
        data object GameTimedOut : Event()
    }

    sealed class Action {
        data object LoadGamePlay : Action()
        data class SubmitOddOneOutVote(val id: String) : Action()
        data class SubmitLocationVote(val location: String) : Action()
    }
}