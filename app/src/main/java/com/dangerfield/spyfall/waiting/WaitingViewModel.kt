package com.dangerfield.spyfall.waiting

import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.api.GameRepository

class WaitingViewModel(private val repository: GameRepository) : ViewModel() {
    val game = repository.currentSession?.game
    val currentUser: String = repository.currentSession?.currentUser.orEmpty()
    val accessCode: String = repository.currentSession?.accessCode.orEmpty()
}