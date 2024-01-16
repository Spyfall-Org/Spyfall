package com.dangerfield.oddoneout.legacy.ui.waiting

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.oddoneout.R
import com.dangerfield.oddoneout.legacy.api.GameRepository
import com.dangerfield.oddoneout.legacy.api.Resource
import com.dangerfield.oddoneout.legacy.models.Session
import com.dangerfield.oddoneout.legacy.ui.game.StartGameError
import com.dangerfield.oddoneout.legacy.util.Event

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

    //user triggered events
    private val nameChangeEvent = MediatorLiveData<Event<Resource<String, NameChangeError>>>()
    private val startGameEvent = MediatorLiveData<Event<Resource<Unit, StartGameError>>>()

    //Globally triggered events
    private val liveGame = repository.getLiveGame(currentSession)
    private val sessionEndedEvent = repository.getSessionEnded()
    private val removeInactiveUserEvent = repository.getRemoveInactiveUserEvent()
    private val leaveGameEvent = repository.getLeaveGameEvent()

    //user triggered events
    fun getNameChangeEvent() = nameChangeEvent
    fun getStartGameEvent() = startGameEvent

    //Global events/data
    fun getLiveGame() = liveGame
    fun getSessionEnded() = sessionEndedEvent
    fun getRemoveInactiveUserEvent() = removeInactiveUserEvent
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
        repository.leaveGame(currentSession)
    }

    fun cancelNameChangeEvent() {
        repository.cancelChangeName()
    }

    fun triggerChangeNameEvent(newName: String, onLoading: (() -> Unit)? = null) {
        if (findNameChangeErrors(newName)) return
        onLoading?.invoke()
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
            currentSession.game.playerList.contains(newName) -> {nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.NAME_IN_USE))
                Log.d("Elijah", "name: $newName was taken")
            }
            currentSession.game.started -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.GAME_STARTED))
            else -> return false
        }
        return true
    }
}
