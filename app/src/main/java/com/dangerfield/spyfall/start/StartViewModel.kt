package com.dangerfield.spyfall.start

import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.api.GameRepository

class StartViewModel(val repository: GameRepository) : ViewModel() {
    fun isCurrentlyInGame() =
        repository.currentSession != null

}