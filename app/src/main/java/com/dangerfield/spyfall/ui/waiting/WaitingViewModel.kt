package com.dangerfield.spyfall.ui.waiting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.util.Event

enum class NameChangeError(val resId: Int) {
    FORMAT_ERROR(R.string.change_name_character_limit),
    NAME_IN_USE(R.string.change_name_different_name),
    NETWORK_ERROR(R.string.newtork_error_message),
    GAME_STARTED(R.string.change_name_error_started_game),
    UNKNOWN_ERROR(R.string.unknown_error)
}

enum class LeaveGameError(val resId: Int) {
    UNKNOWN_ERROR(R.string.unknown_error),
}

class WaitingViewModel(private val repository: GameRepository, val currentSession: Session) : ViewModel() {

    var hasNavigatedUsingSavedSessionToStartedGame = false

    private val nameChangeEvent = MediatorLiveData<Event<Resource<String, NameChangeError>>>()
    private val startGameEvent = MediatorLiveData<Event<Resource<Unit, StartGameError>>>()
    private val leaveGameEvent = MediatorLiveData<Event<Resource<Unit, LeaveGameError>>>()


    //user triggered events
    fun getNameChangeEvent() = nameChangeEvent
    fun getStartGameEvent() = startGameEvent

    //Global events/data
    fun getLiveGame() = repository.getLiveGame(currentSession)
    fun getSessionEnded() = repository.getSessionEnded()
    fun getRemoveInactiveUserEvent() = repository.getRemoveInactiveUserEvent()
    fun getLeaveGameEvent() = leaveGameEvent

    fun triggerStartGameEvent() {
        if(currentSession.game.started) {
            startGameEvent.postValue(Event(Resource.Error(error = StartGameError.GAME_STARTED )))
        } else {
            val repoResult = repository.startGame(currentSession)
            startGameEvent.addSource(repoResult) {
                startGameEvent.postValue(it)
                startGameEvent.removeSource(repoResult)
            }
        }
    }

    fun triggerLeaveGameEvent() {
        repository.cancelChangeName()
        repository.cancelStartGame()
        val repoResult = repository.leaveGame(currentSession)
        leaveGameEvent.addSource(repoResult) {
            leaveGameEvent.postValue(it)
            leaveGameEvent.removeSource(repoResult)
        }
    }

    fun triggerChangeNameEvent(newName: String) {
        if (findNameChangeErrors(newName)) return
        val repoResults = repository.changeName(newName, currentSession)
        nameChangeEvent.addSource(repoResults) {
            nameChangeEvent.value = it
            nameChangeEvent.removeSource(repoResults)
        }
    }

    private fun findNameChangeErrors(newName: String): Boolean {
        when {
            newName.length > 25 ||
                    newName.isEmpty() -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.FORMAT_ERROR))
            currentSession.game.playerList.contains(newName) -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.NAME_IN_USE))
            currentSession.game.started -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.GAME_STARTED))
            else -> return false
        }
        return true
    }
}