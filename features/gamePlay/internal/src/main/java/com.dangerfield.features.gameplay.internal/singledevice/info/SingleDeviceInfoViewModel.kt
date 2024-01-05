package com.dangerfield.features.gameplay.internal.singledevice.info

import androidx.lifecycle.SavedStateHandle
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SingleDeviceInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val clearActiveGame: ClearActiveGame,
): SEAViewModel<Unit,  SingleDeviceInfoViewModel.Event, SingleDeviceInfoViewModel.Action>() {

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    override val initialState = Unit

    override suspend fun handleAction(action: Action) {
        when(action) {
            Action.EndGame -> endGame()
        }
    }

    private suspend fun endGame() {
        gameRepository.end(accessCode)
        // TODO pack game ending behind a uses case and leave game for that matter.
        // not super sure I need a data source as well as a repository
        // maybe I continue trucking on and clean that up later

        clearActiveGame()
        sendEvent(Event.GameEnded)
    }

    sealed class Action {
        object EndGame : Action()
    }

    sealed class Event {
        object GameEnded : Event()
    }


}