package com.dangerfield.features.waitingroom.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.waitingroom.accessCodeArgument
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Event
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.ui.showMessage
import com.dangerfield.oddoneoout.features.waitingroom.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import oddoneout.core.Message
import oddoneout.core.debugSnackOnError
import oddoneout.core.eitherWay
import oddoneout.core.logOnFailure
import oddoneout.core.snackOnError
import oddoneout.core.throwIfDebug
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val startGameUseCase: StartGameUseCase,
    private val session: Session,
    private val dictionary: Dictionary,
    private val clearActiveGame: ClearActiveGame,
    private val leaveGameUseCase: LeaveGameUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val gameConfig: GameConfig,
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    private val isSubscribedToGameFlow = AtomicBoolean(false)

    private val meUserId: String
        get() {
            val userId = session.activeGame?.userId
            return userId.orEmpty()
        }

    private val accessCode: String get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val gameFlow: SharedFlow<Game?> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    init {
        viewModelScope.launch {
            stateFlow
                .map { it.isLoadingStart }
                .debounce(10.seconds)
                .collectLatest { isLoadingStartAfter10Seconds ->
                    if (isLoadingStartAfter10Seconds) {
                        showMessage { dictionary.getString(R.string.blockingError_error_body) }
                        gameRepository.end(accessCode)
                    }
                }
        }
    }

    override suspend fun mapEachState(state: State): State {
        return state.copy(
            didSomethingGoWrong = meUserId.isEmpty()
        )
    }

    override suspend fun handleAction(action: Action) {
        when (action) {
            is Action.LoadRoom -> action.loadRoom()
            is Action.ChangeName -> action.changeName()
            is Action.StartGame -> action.startGame()
            is Action.LeaveGame -> action.leaveGame()
        }
    }

    private suspend fun getGame() =
        gameFlow.replayCache.firstOrNull()
            ?: gameFlow.filterNotNull().firstOrNull()
            ?: gameRepository.getGame(accessCode).getOrNull()


    private suspend fun Action.LeaveGame.leaveGame() {
        val game = getGame()

        if (game == null) {
            Timber.d("Game is null in WaitingRoom while leaving game")
            sendEvent(Event.LeftGame)
            return
        }

        leaveGameUseCase.invoke(
            game = game,
            id = meUserId,
            isGameBeingStarted = state.isLoadingStart
        )
            .eitherWay { sendEvent(Event.LeftGame) }
            .onFailure {
                if (it is GameError.TriedToLeaveStartedGame) {
                    sendEvent(Event.TriedToLeaveStartedGame)
                }
            }
            .logOnFailure()
    }

    private suspend fun Action.StartGame.startGame() {
        updateState { it.copy(isLoadingStart = true) }

        val game = getGame()

        if (game == null) {
            showMessage(
                message =
                Message(
                    message = dictionary.getString(R.string.waitingRoom_forcedRemovalError_text),
                    autoDismiss = false
                )
            )
            takeAction(Action.LeaveGame)
            return
        }

        startGameUseCase(
            id = meUserId,
            currentGame = game
        )
            .logOnFailure()
            // TODO we dont need to snack on every error, some errors should fail silently like
            // if the game is already started while starting
            .snackOnError { dictionary.getString(R.string.blockingError_error_body) }
            .eitherWay {
                updateState { it.copy(isLoadingStart = false) }
            }
    }

    private suspend fun  Action.ChangeName.changeName() {
        gameRepository.changeName(accessCode, name, id)
            .debugSnackOnError { "Error changing name" }
    }

    private suspend fun Action.LoadRoom.loadRoom() {
        if (isSubscribedToGameFlow.getAndSet(true)) return

        viewModelScope.launch {
            gameFlow
                .collectLatest { game ->
                    when (game?.state) {
                        Game.State.Started -> {
                            updateState { it.copy(isLoadingStart = false) }
                            sendEvent(
                                Event.GameStarted(
                                    accessCode = accessCode,
                                    timeLimit = game.timeLimitMins
                                )
                            )
                        }

                        Game.State.Starting -> updateState { it.copy(isLoadingStart = true) }
                        null -> {
                            clearActiveGame.invoke()
                            // game could not exist if the user was removed or left, wait
                            // in case they are still in the process of leaving
                            delay(1000)
                            sendEvent(Event.LeftGame)
                        }

                        Game.State.Waiting -> updateState { state ->
                            val mePlayer =
                                game.players.find { it.id == session.activeGame?.userId }

                            val isMeHost = mePlayer?.isHost == true
                            val players = game.players.map { player ->
                                DisplayablePlayer(
                                    name = player.userName,
                                    isMe = player == mePlayer
                                )
                            }

                            state.copy(
                                isLoadingRoom = false,
                                isLoadingStart = false,
                                players = players,
                                canControlGame = isMeHost || gameConfig.canNonHostsControlGame,
                                videoCallLink = game.videoCallLink
                            )
                        }

                        else -> throwIfDebug { "Got illegal state in waiting room: ${game?.state}" }
                    }
                }
        }
    }

    data class DisplayablePlayer(
        val name: String,
        val isMe: Boolean
    )

    sealed class Action {
        data object LoadRoom : Action()
        data class ChangeName(val name: String, val id: String) : Action()
        data object StartGame : Action()
        data object LeaveGame : Action()
    }

    sealed class Event {
        data class GameStarted(val accessCode: String, val timeLimit: Int) : Event()
        data object TriedToLeaveStartedGame : Event()
        data object LeftGame : Event()
    }

    data class State(
        val accessCode: String,
        val players: List<DisplayablePlayer>,
        val isLoadingRoom: Boolean,
        val canControlGame: Boolean,
        val isLoadingStart: Boolean,
        val didSomethingGoWrong: Boolean,
        val videoCallLink: String?
    )

    override fun initialState() = State(
        accessCode = accessCode,
        players = emptyList(),
        isLoadingRoom = true,
        canControlGame = false,
        isLoadingStart = false,
        didSomethingGoWrong = false,
        videoCallLink = null
    )
}