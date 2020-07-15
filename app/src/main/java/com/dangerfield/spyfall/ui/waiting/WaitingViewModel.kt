package com.dangerfield.spyfall.ui.waiting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.util.Event

enum class NameChangeError(val resId: Int) {
    FORMAT_ERROR(R.string.change_name_character_limit),
    NAME_IN_USE(R.string.change_name_different_name),
    NETWORK_ERROR(R.string.newtork_error_message),
    GAME_STARTED(R.string.change_name_error_started_game),
    UNKNOWN_ERROR(R.string.unknown_error)
}

class WaitingViewModel(private val repository: GameRepository, val currentSession: CurrentSession) : ViewModel() {

    private val nameChangeEvent: MediatorLiveData<Event<Resource<String, NameChangeError>>> =
        MediatorLiveData()

    private val liveGame = repository.getLiveGame()

    private val sessionEnded = repository.getSessionEnded()

    fun getNameChangeEvent() = nameChangeEvent

    fun getLiveGame() = liveGame

    fun getSessionEnded() = sessionEnded

    fun leaveGame() =
        repository.leaveGame(currentSession)

    fun startGame() =
        repository.startGame(currentSession)

    fun fireNameChange(newName: String) {
        if (findNameChangeErrors(newName)) return
        nameChangeEvent.addSource(repository.changeName(newName, currentSession)) {
            nameChangeEvent.value = it
        }
    }

    private fun findNameChangeErrors(newName: String): Boolean {
        when {
            newName.length > 25 ||
                    newName.isEmpty() -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.FORMAT_ERROR))
            currentSession.game.playerList.contains(newName) -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.NAME_IN_USE))
            currentSession.isBeingStarted() -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.GAME_STARTED))
            else -> return false
        }
        return true
    }
}