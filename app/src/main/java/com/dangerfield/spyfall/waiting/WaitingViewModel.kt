package com.dangerfield.spyfall.waiting

import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.api.GameRepository

enum class StartGameError {
    GAME_ALREADY_STARTED,

}
class WaitingViewModel(private val repository: GameRepository) : ViewModel() {
    val game = repository.currentSession?.liveGame
    val currentUser: String = repository.currentSession?.currentUser.orEmpty()
    val accessCode: String = repository.currentSession?.accessCode.orEmpty()

    fun leaveGame() {
        repository.leaveGame()
    }

    fun startGame() {
        repository.startGame()
    }
}