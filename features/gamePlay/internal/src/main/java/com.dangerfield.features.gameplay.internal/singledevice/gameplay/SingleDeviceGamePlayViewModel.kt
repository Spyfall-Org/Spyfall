package com.dangerfield.features.gameplay.internal.singledevice.gameplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.singledevice.SingleDeviceGameMetricTracker
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Action
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Event
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.State
import com.dangerfield.features.gameplay.timeLimitArgument
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.TriggerFlow
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import oddoneout.core.developerSnackIfDebug
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class SingleDeviceGamePlayViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    private val clearActiveGame: ClearActiveGame,
    private val userRepository: UserRepository,
    private val singleDeviceGameMetricTracker: SingleDeviceGameMetricTracker
    ) : SEAViewModel<State, Event, Action>() {

    private val isSubscribedToGameFlow = AtomicBoolean(false)

    // TODO may be concurency issues here, the timer could be stoped or started from different coroutines
    private var timerJob: Job? = null
    private val gameTimeRefreshTrigger = TriggerFlow()
    private val isTimerRunning: Boolean get() = timerJob != null
    private val hasRecordedGamePlayed = AtomicBoolean(false)

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val timeLimitArg: Int?
        get() = savedStateHandle.navArgument<Int>(timeLimitArgument).takeIf { (it ?: 0) > 0 }

    private val gameFlow: SharedFlow<Game> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    override val initialState = State(
        isTimeUp = false,
        timeRemainingMillis = timeLimitArg?.minutes?.inWholeMilliseconds ?: 0
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadGame -> loadGamePlay()
            Action.EndGame -> endGame()
            Action.ResetGame -> resetGame()
        }
    }

    private suspend fun loadGamePlay() {
        if (isSubscribedToGameFlow.getAndSet(true)) return
        viewModelScope.launch {
            combine(
                gameFlow,
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
                    is GameState.Voting,
                    is GameState.VotingEnded -> updateVotingGame()
                }
            }
        }
    }

    private suspend fun updateVotingGame() {
        stopTimer()
        updateState { prev ->
            prev.copy(
                isTimeUp = true,
                timeRemainingMillis = 0L,
            )
        }
    }

    private suspend fun updateInProgressGame(gameState: GameState.Started) {
        if (!isTimerRunning) startTimer()

        updateState { prev ->
            prev.copy(
                timeRemainingMillis = gameState.timeRemainingMillis,
            )
        }

        if (hasRecordedGamePlayed.getAndSet(true)) {
            recordGamePlayed(gameState)
        }
    }

    private suspend fun recordGamePlayed(gameState: GameState.Started) {
        userRepository.addGamePlayed(
            accessCode = gameState.accessCode,
            startedAt = gameState.startedAt,
            wasSingleDevice = true
        )
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

    private suspend fun endGame() {
        gameRepository.end(accessCode)
        // TODO pack game ending behind a uses case and leave game for that matter.
        // not super sure I need a data source as well as a repository
        // maybe I continue trucking on and clean that up later
        clearActiveGame()
        sendEvent(Event.GameKilled)
        singleDeviceGameMetricTracker.trackGameEnded(
            game = getGame(),
            timeRemainingMillis = state.value.timeRemainingMillis
        )
    }

    private suspend fun resetGame() {
        gameRepository.reset(accessCode)
            .onSuccess {
                singleDeviceGameMetricTracker.trackGameRestarted(
                    game = getGame(),
                    timeRemainingMillis = 0
                )
            }.onFailure {
                singleDeviceGameMetricTracker.trackGameRestartError(
                    game = getGame(),
                    timeRemainingMillis = 0
                )
            }
    }

    private suspend fun getGame() = gameFlow.replayCache.firstOrNull() ?: gameFlow.first()

    data class State(
        val isTimeUp: Boolean,
        val timeRemainingMillis: Long
    )

    sealed class Action {
        internal data object LoadGame : Action()
        data object ResetGame : Action()
        data object EndGame : Action()
    }

    sealed class Event {
        data object GameKilled : Event()
        data class GameReset(val accessCode: String) : Event()
    }
}