package com.dangerfield.spyfall.welcome.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import spyfallx.core.GamePrefs
import spyfallx.core.Session
import spyfallx.coregameapi.GameRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferences: GamePrefs,
    private val gameRepository: GameRepository
) : ViewModel() {

    val state: StateFlow<State> = flow {
        val lastKnownSession = preferences.session
        if (lastKnownSession != null && gameRepository.gameExists(lastKnownSession.accessCode)) {
            emit(GameStatus.FoundInGame(lastKnownSession))
        } else {
            preferences.session = null
            emit(GameStatus.NotFoundInGame)
        }
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
