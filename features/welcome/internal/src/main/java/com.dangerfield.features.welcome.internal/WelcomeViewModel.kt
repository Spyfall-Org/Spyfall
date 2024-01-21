package com.dangerfield.features.welcome.internal

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.welcome.internal.WelcomeViewModel.Action
import com.dangerfield.features.welcome.internal.WelcomeViewModel.Event
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import oddoneout.core.developerSnackIfDebug
import oddoneout.core.withBackoffRetry
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val session: Session,
    private val clearActiveGame: ClearActiveGame,
    @Named(MultiDeviceRepositoryName) private val multiDeviceGameRepository: GameRepository,
    @Named(SingleDeviceRepositoryName) private val singleDeviceRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
) : SEAViewModel<Unit, Event, Action>() {

    override val initialState = Unit

    private val gameRepository: GameRepository
        get() = if (session.activeGame?.isSingleDevice == true) singleDeviceRepository else multiDeviceGameRepository

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.CheckForActiveGame -> checkForActiveGame()
        }
    }


    val dummyState = flow {
        emit(1)
        delay(1000)
        emit(2)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )

    /*
    TODO cleanup I could probably have a provider of the repo that changes based on the sessions active game being online or
    not
     */
    private suspend fun checkForActiveGame() {
        val activeGame = session.activeGame
        if (activeGame != null) {
            withBackoffRetry(
                retries = 3,
                initialDelayMillis = 500,
                factor = 2.0,
            ) {
                gameRepository.getGame(activeGame.accessCode)
            }
                .map { game ->
                    mapToGameState(game.accessCode, game)
                }
                .onSuccess {
                    developerSnackIfDebug {
                        "Found game with state: ${it::class.java.simpleName}. Access code: ${activeGame.accessCode}"
                    }

                    when (it) {
                        is GameState.Expired -> {
                            if (!activeGame.isSingleDevice) {
                                gameRepository.end(activeGame.accessCode)
                            }
                            clearActiveGame()
                        }

                        is GameState.Waiting,
                        is GameState.Starting -> {
                            if (activeGame.isSingleDevice) {
                                sendEvent(Event.GameInSingleDeviceRoleRevealFound(activeGame.accessCode))
                            } else {
                                sendEvent(Event.GameInWaitingRoomFound(activeGame.accessCode))
                            }
                        }

                        is GameState.Unknown,
                        is GameState.DoesNotExist -> clearActiveGame()

                        is GameState.Started,
                        is GameState.Voting,
                        is GameState.VotingEnded -> {
                            sendEvent(
                                Event.GameInProgressFound(
                                    activeGame.accessCode,
                                    activeGame.isSingleDevice
                                )
                            )
                        }
                    }
                }
        }
    }

    sealed class Action {
        data object CheckForActiveGame : Action()
    }

    sealed class Event {
        data class GameInSingleDeviceRoleRevealFound(val accessCode: String) : Event()
        data class GameInWaitingRoomFound(val accessCode: String) : Event()
        data class GameInProgressFound(val accessCode: String, val isSingleDevice: Boolean) :
            Event()
    }
}
