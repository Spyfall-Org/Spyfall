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
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import oddoneout.core.showDebugSnack
import oddoneout.core.eitherWay
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class GamePlayViewModel @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    private val clearActiveGame: ClearActiveGame,
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val metricsTracker: MultiDeviceGameMetricsTracker,
    private val gameConfig: GameConfig,
    session: Session,
) :SEAViewModel<State, Event, Action>(savedStateHandle) {

    private val meUserId = session.activeGame?.userId ?: ""
    private val isSubscribedToGameFlow = AtomicBoolean(false)
    private val hasRecordedResult = AtomicBoolean(false)
    private val hasRecordedGamePlayed = AtomicBoolean(false)

    // TODO cleanup may be concurency issues here, the timer could be stoped or started from different coroutines
    private var timerJob: Job? = null
    private val gameTimeRefreshTrigger = TriggerFlow()
    private val isTimerRunning: Boolean get() = timerJob != null

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

    // TODO cleanup consider putting voting into a different screen
    override fun initialState() = State(
        players = emptyList(),
        locations = emptyList(),
        timeRemainingMillis = timeLimitArg?.minutes?.inWholeMilliseconds ?: 0,
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
        videoCallLink = null,
        canControlGame = false,
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            is Action.LoadGamePlay -> action.loadGamePlay()
            is Action.SubmitLocationVote -> action.submitLocationVote()
            is Action.SubmitOddOneOutVote -> action.submitOddOneOutVote()
            is Action.ResetGame -> action.resetGame()
            is Action.EndGame -> endGame()
        }
    }

    private suspend fun endGame() {
        metricsTracker.trackGameEnded(
            game = getGame(),
            timeRemainingMillis = state.timeRemainingMillis
        )
        gameRepository.end(accessCode)
    }

    private suspend fun Action.ResetGame.resetGame() {
        gameRepository.reset(accessCode)
            .onSuccess {
                metricsTracker.trackGameRestarted(
                    game = getGame(),
                    timeRemainingMillis = state.timeRemainingMillis
                )
            }
            .onFailure {
                metricsTracker.trackGameRestartError(
                    game = getGame(),
                    timeRemainingMillis = state.timeRemainingMillis
                )

                sendEvent(Event.ResetFailed)
            }
            .logOnFailure()
            .throwIfDebug()
    }

    private suspend fun Action.SubmitOddOneOutVote.submitOddOneOutVote() {
        updateState { it.copy(isLoadingVoteSubmit = true) }
        gameRepository.submitOddOneOutVote(
            accessCode,
            voterId = meUserId,
            voteId = id
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

    private suspend fun Action.SubmitLocationVote.submitLocationVote() {
        updateState { it.copy(isLoadingVoteSubmit = true) }
        gameRepository.submitLocationVote(
            accessCode,
            voterId = meUserId,
            location = location
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

    // TODO cleanup consider having individual functions gameRepository.getGameFlow.waitUntil(STATE)
    private suspend fun Action.LoadGamePlay.loadGamePlay() {
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
                    is GameState.Unknown -> showDebugSnack {
                        "Illegal game state ${gameState::class.java.simpleName}"
                    }

                    is GameState.DoesNotExist -> {
                        clearActiveGame()
                        sendEvent(Event.GameKilled)
                    }

                    is GameState.Waiting -> sendEvent(Event.GameReset(accessCode))
                    is GameState.Started -> updateStateInProgressGame(gameState)
                    is GameState.Voting -> updateStateVotingGame(gameState)
                    is GameState.VotingEnded -> updateStateVotingEndedGame(gameState)
                }
            }
        }
    }

    private suspend fun Action.LoadGamePlay.updateStateVotingEndedGame(
        gameState: GameState.VotingEnded,
    ) {
        stopTimer()

        val mePlayer = gameState.players.find { it.id == meUserId }

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
                mePlayer = mePlayer
                    ?.let { me ->
                        DisplayablePlayer(
                            name = me.userName,
                            isFirst = false,
                            id = me.id,
                            role = me.role ?: "",
                            isOddOneOut = me.isOddOneOut
                        )
                    },
                canControlGame = mePlayer?.isHost == true || gameConfig.canNonHostsControlGame,
                locations = gameState.locationNames,
                location = gameState.location,
                isTimeUp = true,
                gameResult = gameState.result,
            )
        }

        if (!hasRecordedResult.getAndSet(true)) {
            recordResult(gameState)

            if (gameState.mePlayer.isHost) {
                metricsTracker.trackVotingEnded(
                    timeLimitMins = gameState.timeLimitMins,
                    gameState = gameState
                )
            }
        }
    }

    private suspend fun recordResult(gameState: GameState.VotingEnded) {
        val mePlayer = gameState.players.find { it.id == meUserId } ?: return
        val didWinAsOddOne = gameState.result == GameResult.OddOneOutWon && mePlayer.isOddOneOut
        val didWinAsPlayer = gameState.result == GameResult.PlayersWon && !mePlayer.isOddOneOut
        userRepository.addUsersGameResult(
            wasOddOneOut = mePlayer.isOddOneOut,
            didWin = didWinAsPlayer || didWinAsOddOne,
            accessCode = gameState.accessCode,
            startedAt = gameState.startedAt
        )
    }

    private suspend fun recordGamePlayed(gameState: GameState.Started) {
        userRepository.addGamePlayed(
            accessCode = gameState.accessCode,
            startedAt = gameState.startedAt,
            wasSingleDevice = false
        )
    }

    private suspend fun Action.LoadGamePlay.updateStateVotingGame(
        gameState: GameState.Voting,
    ) {
        stopTimer()

        val mePlayer = gameState.players.find { it.id == meUserId }

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
                mePlayer = mePlayer
                    ?.let { me ->
                        DisplayablePlayer(
                            name = me.userName,
                            isFirst = false,
                            id = me.id,
                            role = me.role ?: "",
                            isOddOneOut = me.isOddOneOut
                        )
                    },
                canControlGame = mePlayer?.isHost == true || gameConfig.canNonHostsControlGame,
                locations = gameState.locationNames,
                location = gameState.location,
                isTimeUp = true

            )
        }
    }

    private suspend fun Action.LoadGamePlay.updateStateInProgressGame(
        gameState: GameState.Started,
    ) {
        if (!isTimerRunning) startTimer()

        val mePlayer = gameState.players.find { it.id == meUserId }

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
                mePlayer = mePlayer?.let { me ->
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
                canControlGame = mePlayer?.isHost == true || gameConfig.canNonHostsControlGame,
                timeRemainingMillis = gameState.timeRemainingMillis,
            )
        }

        if (!hasRecordedGamePlayed.getAndSet(true)) {
            recordGamePlayed(gameState)
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startTimer() {
        if (isTimerRunning) return
        timerJob = viewModelScope.launch {
            // TODO cleanup should this run on viewmodel scope? infact maybe look at all viewmodel scopes
            while (isActive) {
                gameTimeRefreshTrigger.pull()
                delay(500)
            }
        }
    }

    private suspend fun getGame() =
        gameFlow.replayCache.firstOrNull()
            ?: gameFlow.filterNotNull().firstOrNull()
            ?: gameRepository.getGame(accessCode).getOrNull()

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
        val timeRemainingMillis: Long,
        val didSomethingGoWrongLoading: Boolean,
        val didSomethingGoWrongVoting: Boolean,
        val videoCallLink: String?,
        val gameResult: GameResult?,
        val canControlGame: Boolean,
    )

    sealed class Event {
        data class GameReset(val accessCode: String) : Event()
        data object GameKilled : Event()
        data object ResetFailed: Event()
    }

    sealed class Action {
        data object ResetGame : Action()
        data object LoadGamePlay : Action()
        data object EndGame : Action()
        data class SubmitOddOneOutVote(val id: String) : Action()
        data class SubmitLocationVote(val location: String) : Action()
    }
}