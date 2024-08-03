package com.dangerfield.features.gameplay.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Event
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.State
import com.dangerfield.features.gameplay.timeLimitArgument
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UserRepository
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
import oddoneout.core.eitherWay
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug
import java.time.Clock
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/*
TODO cleanup
okay so theres alot going on there
I think the packItem could probably be a part of the game exposed
I think we could also expose game state in there too and not have to have a seperate use case and
make people deal with that.
 */
@HiltViewModel
class GamePlayViewModel @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val clearActiveGame: ClearActiveGame,
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val metricsTracker: MultiDeviceGameMetricsTracker,
    private val gameConfig: GameConfig,
    private val clock: Clock,
    session: Session,
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    private val meUserId = session.activeGame?.userId ?: ""
    private val isSubscribedToGameFlow = AtomicBoolean(false)
    private val hasRecordedResult = AtomicBoolean(false)
    private val hasRecordedGamePlayed = AtomicBoolean(false)
    private val backupGameStartTime = clock.millis()

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
        players = emptyList(),
        locations = emptyList(),
        timeRemainingMillis = timeLimitArg?.seconds?.inWholeMilliseconds ?: 0,
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
        packItem = null
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
        gameRepository.submitVoteForOddOneOut(
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
        gameRepository.submitVoteForSecret(
            accessCode,
            voterId = meUserId,
            secret = location
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
            gameFlow.map { game ->
                when (game?.state) {
                    Game.State.Starting,
                    Game.State.Expired,
                    Game.State.Unknown -> showDebugSnack {
                        "Illegal game state with game $game"
                    }

                    null -> {
                        clearActiveGame()
                        sendEvent(Event.GameKilled)
                    }

                    Game.State.Waiting -> sendEvent(Event.GameReset(accessCode))
                    is Game.State.Started -> updateStateWithGame(game, false)
                    Game.State.Voting -> updateStateWithGame(game, true)
                    Game.State.Results -> updateStateWithGame(game, true)
                }
            }.collect()
        }
    }

    private suspend fun Action.updateStateWithGame(game: Game, isTimeUp: Boolean) {

        if (game.state == Game.State.Results && !hasRecordedResult.getAndSet(true)) {
            recordResult(game)

            if (game.mePlayer?.isHost == true) {
                metricsTracker.trackVotingEnded(
                    game = game
                )
            }
        }

        if (!hasRecordedGamePlayed.getAndSet(true)) {
            recordGamePlayed(game)
        }

        val remainingMillis = (game.state as? Game.State.Started)?.let { startedState ->
            val remainingSeconds = game.timeLimitSeconds - startedState.secondsElapsed
            remainingSeconds.seconds.inWholeMilliseconds
        } ?: 0

        updateState { prev ->
            prev.copy(
                isLoadingPlayers = false,
                isLoadingLocations = false,
                videoCallLink = game.videoCallLink,
                packItem = game.secretItem,
                players = game.players.map { player ->
                    DisplayablePlayer(
                        name = player.userName,
                        isFirst = game.players.firstOrNull() == player,
                        id = player.id,
                        role = player.role ?: "",
                        isOddOneOut = player.isOddOneOut
                    )
                },
                mePlayer = game.mePlayer
                    ?.let { me ->
                        DisplayablePlayer(
                            name = me.userName,
                            isFirst = game.players.firstOrNull() == me,
                            id = me.id,
                            role = me.role ?: "",
                            isOddOneOut = me.isOddOneOut
                        )
                    },
                canControlGame = game.mePlayer?.isHost == true || gameConfig.canNonHostsControlGame,
                locations = game.secretOptions,
                location = game.secretItem.name,
                isTimeUp = isTimeUp,
                gameResult = game.result.takeIf {
                    it in listOf(GameResult.PlayersWon, GameResult.OddOneOutWon, GameResult.Draw)
                },
                timeRemainingMillis = remainingMillis,
            )
        }
    }

    private suspend fun recordResult(game: Game) {
        val mePlayer = game.players.find { it.id == meUserId } ?: return
        val didWinAsOddOne = game.result == GameResult.OddOneOutWon && mePlayer.isOddOneOut
        val didWinAsPlayer = game.result == GameResult.PlayersWon && !mePlayer.isOddOneOut
        userRepository.addUsersGameResult(
            wasOddOneOut = mePlayer.isOddOneOut,
            didWin = didWinAsPlayer || didWinAsOddOne,
            accessCode = game.accessCode,
            startedAt = game.startedAt ?: backupGameStartTime
        )
    }

    private suspend fun recordGamePlayed(game: Game) {
        userRepository.addGamePlayed(
            accessCode = game.accessCode,
            startedAt = game.startedAt ?: backupGameStartTime,
            wasSingleDevice = false
        )
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
        val packItem: PackItem?,
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
        data object ResetFailed : Event()
    }

    sealed class Action {
        data object ResetGame : Action()
        data object LoadGamePlay : Action()
        data object EndGame : Action()
        data class SubmitOddOneOutVote(val id: String) : Action()
        data class SubmitLocationVote(val location: String) : Action()
    }
}