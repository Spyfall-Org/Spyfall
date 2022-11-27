package com.dangerfield.spyfall.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import spyfallx.coregameapi.Session
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getGameInProgress: GetGameInProgress,
) : ViewModel() {

    val state: StateFlow<State> = flow {
        getGameInProgress()?.let {
            emit(GameStatus.FoundInGame(it))
        } ?: emit(GameStatus.NotFoundInGame)
    }
        .map { State(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = State(GameStatus.SearchingForGame)
        )

    data class State(
        val gameStatus: GameStatus
    )

    sealed class GameStatus {
        object SearchingForGame : GameStatus()
        object NotFoundInGame : GameStatus()
        class FoundInGame(val session: Session) : GameStatus()
    }
}
