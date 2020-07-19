package com.dangerfield.spyfall.ui.start

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.util.Event
import com.dangerfield.spyfall.util.SavedSessionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class SavedSession(val session: Session, val started: Boolean)

class StartViewModel(val savedSessionHelper : SavedSessionHelper) : ViewModel() {

    private val foundUserInExistingGame = MutableLiveData<Event<SavedSession>>()

    fun getFoundUserInExistingGame() = foundUserInExistingGame

    fun searchForUserInExistingGame() {
        CoroutineScope(Dispatchers.IO).launch {
            savedSessionHelper.whenUserIsInExistingGame {session, started ->
               foundUserInExistingGame.postValue(Event(SavedSession(session, started)))
            }
        }
    }
}