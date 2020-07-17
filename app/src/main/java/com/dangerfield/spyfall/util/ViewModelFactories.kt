package com.dangerfield.spyfall.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.ui.game.GameViewModel
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.waiting.WaitingViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class WaitingViewModelFactory( val currentSession: Session) :
    ViewModelProvider.Factory, KoinComponent {

    val repository: GameRepository by inject()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WaitingViewModel(repository, currentSession) as T
    }
}

class GameViewModelFactory( val currentSession: Session) :
    ViewModelProvider.Factory, KoinComponent {

    val repository: GameRepository by inject()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameViewModel(repository, currentSession) as T
    }
}