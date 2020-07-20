package com.dangerfield.spyfall.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.models.Session
import kotlinx.coroutines.*

class RemoveUserTimer(val repository: GameRepository, private val preferencesHelper: PreferencesHelper) : LifecycleObserver {

    private var removeUserJob: Job = Job()
    private val fifteenMins = 900000L
    private val currentSession : Session?
    get() { return preferencesHelper.getSavedSession() }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        stopTimerToRemoveUser()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        currentSession?.let {
            startTimerToRemoveUser(it)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyed() {
        stopTimerToRemoveUser()
        currentSession?.let {
            repository.removeInactiveUser(it)
        }
    }

    private fun stopTimerToRemoveUser() {
        if (removeUserJob.isActive) {
            try {
                removeUserJob.cancel()
            } catch (e: CancellationException) { }
        }
    }

    private fun startTimerToRemoveUser(currentSession: Session) {
        removeUserJob = GlobalScope.launch {
            delay(fifteenMins)
            repository.removeInactiveUser(currentSession)
        }
    }
}