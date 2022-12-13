package com.dangerfield.spyfall.splash.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.spyfall.splash.forcedupdate.IsUpdateRequired
import com.dangerfield.spyfall.splash.splash.SplashViewModel.GameStatus.FoundInGame
import com.dangerfield.spyfall.splash.splash.SplashViewModel.GameStatus.NotFoundInGame
import com.dangerfield.spyfall.splash.splash.SplashViewModel.GameStatus.SearchingForGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import spyfallx.coregameapi.Session
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUpdateRequired: IsUpdateRequired,
    private val getGameInProgress: GetGameInProgress
) : ViewModel() {

    val state: StateFlow<State> = flow {
        emit(
            State(
                isUpdateRequired = isUpdateRequired(),
                gameStatus = getGameInProgress()?.let { FoundInGame(it) } ?: NotFoundInGame
            )
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = State(
                isUpdateRequired = false,
                gameStatus = SearchingForGame
            )
        )

    data class State(
        val gameStatus: GameStatus,
        val isUpdateRequired: Boolean
    )

    sealed class GameStatus {
        object SearchingForGame : GameStatus()
        object NotFoundInGame : GameStatus()
        class FoundInGame(val session: Session) : GameStatus()
    }
}
