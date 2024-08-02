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
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import oddoneout.core.showDebugSnack
import java.time.Clock
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SingleDeviceGamePlayViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val clearActiveGame: ClearActiveGame,
    private val userRepository: UserRepository,
    private val singleDeviceGameMetricTracker: SingleDeviceGameMetricTracker,
    private val clock: Clock
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    private val isSubscribedToGameFlow = AtomicBoolean(false)

    private var timerJob: Job? = null
    private val gameTimeRefreshTrigger = TriggerFlow()
    private val isTimerRunning: Boolean get() = timerJob != null
    private val hasRecordedGamePlayed = AtomicBoolean(false)

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val timeLimitArg: Int?
        get() = savedStateHandle.navArgument<Int>(timeLimitArgument).takeIf { (it ?: 0) > 0 }

    private val gameFlow: SharedFlow<Game?> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    override fun initialState() = State(
        isTimeUp = false,
        timeRemainingMillis = timeLimitArg?.minutes?.inWholeMilliseconds ?: 0,
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            is Action.LoadGame -> action.loadGamePlay()
            is Action.EndGame -> endGame()
            is Action.ResetGame -> resetGame()
        }
    }

    private suspend fun Action.LoadGame.loadGamePlay() {
        if (isSubscribedToGameFlow.getAndSet(true)) return
        viewModelScope.launch {
            combine(
                gameFlow,
                gameTimeRefreshTrigger
            ) { game, _ ->
                when (game?.state) {
                    Game.State.Starting,
                    Game.State.Expired,
                    Game.State.Unknown -> showDebugSnack {
                        "Illegal game state with game: \n${game}"
                    }

                    null -> {
                        clearActiveGame()
                        sendEvent(Event.GameKilled)
                    }

                    Game.State.Waiting -> sendEvent(Event.GameReset(accessCode))
                    Game.State.Started,
                    Game.State.Voting,
                    Game.State.Results -> updateStateWithGame(game)
                }
            }
                .collect()
        }
    }

    private suspend fun Action.LoadGame.updateStateWithGame(game: Game) {
        val timeRemainingMillis = game.remainingTimeMillis(clock)
        if (timeRemainingMillis <= 0 && isTimerRunning) {
            stopTimer()
            gameRepository.refreshState()
        }

        if (timeRemainingMillis > 0 && !isTimerRunning) {
            startTimer()
        }

        updateState { prev ->
            prev.copy(
                isTimeUp = timeRemainingMillis <= 0,
                timeRemainingMillis = timeRemainingMillis,
            )
        }

        if (hasRecordedGamePlayed.getAndSet(true)) {
            recordGamePlayed(game)
        }
    }

    private suspend fun recordGamePlayed(game: Game) {
        userRepository.addGamePlayed(
            accessCode = game.accessCode,
            startedAt = game.startedAt ?: clock.millis(),
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
        clearActiveGame()
        sendEvent(Event.GameKilled)
        singleDeviceGameMetricTracker.trackGameEnded(
            game = getGame(),
            timeRemainingMillis = state.timeRemainingMillis
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
        val isTimeUp: Boolean,
        val timeRemainingMillis: Long,
    )

    sealed class Action {
        internal data object LoadGame : Action()
        data object ResetGame : Action()
        data object EndGame : Action()
    }

    sealed class Event {
        data object GameKilled : Event()
        data class GameReset(val accessCode: String) : Event()
        data object ResetFailed : Event()
    }
}