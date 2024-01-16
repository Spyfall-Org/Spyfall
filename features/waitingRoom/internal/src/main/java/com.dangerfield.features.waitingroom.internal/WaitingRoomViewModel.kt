package com.dangerfield.features.waitingroom.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.waitingroom.accessCodeArgument
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Event
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import spyfallx.core.developerSnackOnError
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
    private val startGameUseCase: StartGameUseCase,
    private val session: Session,
    private val leaveGameUseCase: LeaveGameUseCase,
    private val savedStateHandle: SavedStateHandle,
) : SEAViewModel<State, Event, Action>() {

    private val isSubscribedToGameFlow = AtomicBoolean(false)

    private val meUserId: String
        get() {
            val userId = session.activeGame?.userId
            return userId.orEmpty()
        }

    private val accessCode: String get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    override val initialState = State(
        accessCode = accessCode,
        players = emptyList(),
        isLoadingRoom = true,
        isLoadingStart = false,
        didSomethingGoWrong = accessCode.isEmpty(),
        videoCallLink = null
    )

    private val gameFlow: SharedFlow<Game> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    override suspend fun handleAction(action: Action) {
        if (meUserId.isEmpty()) {
            updateState {
                it.copy(didSomethingGoWrong = true)
            }
        }

        when (action) {
            is Action.LoadRoom -> loadRoom()
            is Action.ChangeName -> changeName(action.name, action.id)
            Action.StartGame -> startGame()
            Action.LeaveGame -> leaveGame()
        }
    }

    private suspend fun leaveGame() {
        val game = gameFlow.replayCache.firstOrNull() ?: gameFlow.first()

        leaveGameUseCase.invoke(
            game = game,
            id = meUserId,
            isGameBeingStarted = state.value.isLoadingStart
        )
            .onSuccess { sendEvent(Event.LeftGame) }
            .onFailure {
                if (it is GameError.TriedToLeaveStartedGame) {
                    sendEvent(Event.TriedToLeaveStartedGame)
                }
            }
            .logOnError()
    }

    private suspend fun startGame() {
        updateState { it.copy(isLoadingStart = true) }
        val game = gameFlow.replayCache.firstOrNull() ?: gameFlow.first()
        startGameUseCase(
            accessCode = accessCode,
            players = game.players,
            locationName = game.locationName,
            id = meUserId
        )
            .developerSnackOnError { "Error starting game" }
            .eitherWay {
                updateState { it.copy(isLoadingStart = false) }
            }
    }

    private suspend fun changeName(name: String, id: String) {
        gameRepository.changeName(accessCode, name, id)
            .developerSnackOnError { "Error changing name" }
    }

    private suspend fun loadRoom() {
        if (isSubscribedToGameFlow.getAndSet(true)) return

        viewModelScope.launch {
            gameFlow
                .map { mapToGameState(accessCode, it) }
                .collectLatest { gameState ->
                    when (gameState) {
                        is GameState.Started -> {
                            updateState { it.copy(isLoadingStart = false) }
                            sendEvent(
                                Event.GameStarted(
                                    accessCode = accessCode,
                                    timeLimit = gameState.timeLimitMins
                                )
                            )
                        }

                        is GameState.Starting -> updateState { it.copy(isLoadingStart = true) }
                        is GameState.Waiting -> updateState { state ->
                            val mePlayer =
                                gameState.players.find { it.id == session.activeGame?.userId }
                            val players = gameState.players.map { player ->
                                DisplayablePlayer(
                                    name = player.userName,
                                    isMe = player == mePlayer
                                )
                            }

                            state.copy(
                                isLoadingRoom = false,
                                isLoadingStart = false,
                                players = players,
                                videoCallLink = gameState.videoCallLink
                            )
                        }

                        else -> throwIfDebug { "Got illegal state in waiting room: $gameState" }
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
        val isLoadingStart: Boolean,
        val didSomethingGoWrong: Boolean,
        val videoCallLink: String?
    )
}