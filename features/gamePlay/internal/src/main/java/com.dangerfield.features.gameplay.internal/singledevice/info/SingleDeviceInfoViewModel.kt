package com.dangerfield.features.gameplay.internal.singledevice.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.singledevice.SingleDeviceGameMetricTracker
import com.dangerfield.features.gameplay.internal.singledevice.info.SingleDeviceInfoViewModel.Action
import com.dangerfield.features.gameplay.internal.singledevice.info.SingleDeviceInfoViewModel.Event
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SingleDeviceInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val clearActiveGame: ClearActiveGame,
    private val singleDeviceGameMetricTracker: SingleDeviceGameMetricTracker,
): SEAViewModel<Unit, Event, Action>(savedStateHandle, Unit) {

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val gameFlow: SharedFlow<Game?> = gameRepository
        .getGameFlow(accessCode)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

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
        singleDeviceGameMetricTracker.trackGameEnded(
            game = getGame(),
            timeRemainingMillis = 0
        )
    }

    private suspend fun getGame() =
        gameFlow.replayCache.firstOrNull()
            ?: gameFlow.filterNotNull().firstOrNull()
            ?: gameRepository.getGame(accessCode).getOrNull()

    sealed class Action {
        object EndGame : Action()
    }

    sealed class Event {
        object GameEnded : Event()
    }


}