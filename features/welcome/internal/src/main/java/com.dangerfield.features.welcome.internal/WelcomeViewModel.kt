package com.dangerfield.features.welcome.internal

import com.dangerfield.features.welcome.internal.WelcomeViewModel.Action
import com.dangerfield.features.welcome.internal.WelcomeViewModel.Event
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import spyfallx.core.developerSnackIfDebug
import spyfallx.core.withBackoffRetry
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val session: Session,
    private val clearActiveGame: ClearActiveGame,
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val mapToGameState: MapToGameStateUseCase,
) : SEAViewModel<Unit, Event, Action>() {

    override val initialState = Unit

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.CheckForActiveGame -> checkForActiveGame()
        }
    }

    private suspend fun checkForActiveGame() {
        val activeGame = session.activeGame
        if (activeGame != null) {
            withBackoffRetry(
                retries = 3,
                initialDelayMillis = 500,
                factor = 2.0,
            ) {
                gameRepository
                    .getGame(activeGame.accessCode)
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
                            gameRepository.end(activeGame.accessCode)
                            clearActiveGame()
                        }

                        is GameState.Waiting,
                        is GameState.Starting -> {
                            sendEvent(Event.GameInWaitingRoomFound(activeGame.accessCode))
                        }

                        is GameState.Unknown,
                        is GameState.DoesNotExist -> clearActiveGame()

                        is GameState.Started,
                        is GameState.Voting,
                        is GameState.VotingEnded -> {
                            sendEvent(Event.GameInProgressFound(activeGame.accessCode))
                        }
                    }
                }
        }
    }

    sealed class Action {
        data object CheckForActiveGame : Action()
    }

    sealed class Event {
        data class GameInWaitingRoomFound(val accessCode: String) : Event()
        data class GameInProgressFound(val accessCode: String) : Event()
    }
}
