package com.dangerfield.spyfall.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.spyfall.splash.SplashViewModel.GameStatus.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import spyfallx.coregameapi.Session
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getGameInProgress: GetGameInProgress,
    private val checkForRequiredUpdate: CheckForRequiredUpdate
) : ViewModel() {

    val state: StateFlow<State> = flow<State> {
        val isUpdateRequired = checkForRequiredUpdate.shouldRequireUpdate()
        val gameInProgress = getGameInProgress()
        emit(
            State(
                isUpdateRequired = isUpdateRequired,
                gameStatus = gameInProgress?.let { FoundInGame(it) } ?: NotFoundInGame
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
