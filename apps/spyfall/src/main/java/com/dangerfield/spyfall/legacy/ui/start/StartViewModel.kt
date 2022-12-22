package com.dangerfield.spyfall.legacy.ui.start

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.legacy.api.GameRepository
import com.dangerfield.spyfall.legacy.api.Resource
import com.dangerfield.spyfall.legacy.models.Session
import com.dangerfield.spyfall.legacy.util.Event
import com.dangerfield.spyfall.legacy.util.SavedSessionHelper

data class SavedSession(val session: Session, val started: Boolean)

class StartViewModel(private val savedSessionHelper: SavedSessionHelper, private val repository: GameRepository) : ViewModel() {

    private val searchForUserInGameEvent = MediatorLiveData<Event<Resource<SavedSession, Unit>>>()
    private val leaveGameEvent = repository.getLeaveGameEvent()

    fun getLeaveGameEvent() = leaveGameEvent

    fun getSearchForUserInGameEvent() = searchForUserInGameEvent

    fun triggerSearchForUserInExistingGame() {
        val result = savedSessionHelper.findUserInExistingGame()
        searchForUserInGameEvent.addSource(result) {
            searchForUserInGameEvent.postValue(Event(it))
            searchForUserInGameEvent.removeSource(result)
        }
    }

    fun removeUserFromSession(session: Session) {
        repository.leaveGame(session)
    }
}
