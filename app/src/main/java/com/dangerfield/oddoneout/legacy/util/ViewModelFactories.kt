package com.dangerfield.oddoneout.legacy.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dangerfield.oddoneout.legacy.api.GameRepository
import com.dangerfield.oddoneout.legacy.ui.game.GameViewModel
import com.dangerfield.oddoneout.legacy.models.Session
import com.dangerfield.oddoneout.legacy.ui.waiting.WaitingViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WaitingViewModelFactory( val currentSession: Session) :
    ViewModelProvider.Factory, KoinComponent {

    val repository: GameRepository by inject()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WaitingViewModel(repository, currentSession) as T
    }
}

class GameViewModelFactory( val currentSession: Session) :
    ViewModelProvider.Factory, KoinComponent {

    val repository: GameRepository by inject()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(repository, currentSession) as T
    }
}
