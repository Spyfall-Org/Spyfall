package com.dangerfield.features.gameplay.internal.singledevice.gameplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.State
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Event
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Action
import com.dangerfield.features.gameplay.timeLimitArgument
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.TriggerFlow
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import spyfallx.core.developerSnackIfDebug
import spyfallx.core.doNothing
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


    override val initialState = State(
        isTimeUp = false,
        timeRemaining = timeLimitArg?.minutes?.inWholeMilliseconds?.millisToMMss() ?: ""
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
                timeRemaining = 0L.millisToMMss(),
            )
        }
    }

    private suspend fun updateInProgressGame(gameState: GameState.Started) {
        if (!isTimerRunning) startTimer()

        updateState { prev ->
            prev.copy(
                timeRemaining = gameState.timeRemainingMillis.millisToMMss(),
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

    @Suppress("ImplicitDefaultLocale")
    private fun Long.millisToMMss(): String {
        val minutes = this / 60000
        val seconds = (this % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
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

    data class State(
        val isTimeUp: Boolean,
        val timeRemaining: String
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