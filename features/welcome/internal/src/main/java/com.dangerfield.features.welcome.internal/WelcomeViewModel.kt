package com.dangerfield.features.welcome.internal

import androidx.lifecycle.SavedStateHandle
import com.dangerfield.features.welcome.internal.WelcomeViewModel.Action
import com.dangerfield.features.welcome.internal.WelcomeViewModel.Event
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.SessionFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import oddoneout.core.showDebugSnack
import oddoneout.core.withBackoffRetry
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val sessionFlow: SessionFlow,
    private val session: Session,
    private val clearActiveGame: ClearActiveGame,
    @Named(MultiDeviceRepositoryName) private val multiDeviceGameRepository: GameRepository,
    @Named(SingleDeviceRepositoryName) private val singleDeviceRepository: GameRepository,
    savedStateHandle: SavedStateHandle
) : SEAViewModel<Unit, Event, Action>(savedStateHandle, Unit) {

    private val gameRepository: GameRepository
        get() = if (session.activeGame?.isSingleDevice == true) singleDeviceRepository else multiDeviceGameRepository

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.CheckForActiveGame -> checkForActiveGame()
        }
    }

    private suspend fun checkForActiveGame() {
        val activeGame = sessionFlow.first().activeGame

        if (activeGame != null) {
            withBackoffRetry(
                retries = 3,
                initialDelayMillis = 500,
                factor = 2.0,
            ) {
                gameRepository.getGame(activeGame.accessCode)
            }
                .onSuccess { game ->
                    showDebugSnack {
                        "Found game with state: ${game.state::class.java.simpleName}. Access code: ${activeGame.accessCode}"
                    }

                    when (game.state) {
                        Game.State.Expired -> {
                            if (!activeGame.isSingleDevice) {
                                gameRepository.end(activeGame.accessCode)
                            }
                            clearActiveGame()
                        }

                        Game.State.Waiting,
                        Game.State.Starting -> {
                            if (activeGame.isSingleDevice) {
                                sendEvent(Event.GameInSingleDeviceRoleRevealFound(activeGame.accessCode))
                            } else {
                                sendEvent(Event.GameInWaitingRoomFound(activeGame.accessCode))
                            }
                        }

                        Game.State.Unknown,
                        Game.State.Started,
                        Game.State.Voting,
                        Game.State.Results -> {
                            sendEvent(
                                Event.GameInProgressFound(
                                    activeGame.accessCode,
                                    activeGame.isSingleDevice
                                )
                            )
                        }
                    }
                }.
                    onFailure { clearActiveGame() }
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
