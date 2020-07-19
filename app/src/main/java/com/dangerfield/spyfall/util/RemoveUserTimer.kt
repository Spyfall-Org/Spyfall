package com.dangerfield.spyfall.util

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.models.Session
import kotlinx.coroutines.*

class RemoveUserTimer(val repository: GameRepository, private val preferencesHelper: PreferencesHelper) : LifecycleObserver {

    private var removeUserJob: Job = Job()
    private val fifteenMins = 900000L
    private val tenSeconds = 10000L
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
            Log.d("Elijah", "removing user")
            repository.removeInactiveUser(it)
        }
    }

    private fun stopTimerToRemoveUser() {
        Log.d("Elijah", "Stopping timer to remove user")

        if (removeUserJob.isActive) {
            try {
                removeUserJob.cancel()
            } catch (e: CancellationException) { }
        }
    }

    private fun startTimerToRemoveUser(currentSession: Session) {
        Log.d("Elijah", "Starting timer to remove user")

        removeUserJob = GlobalScope.launch {
            delay(tenSeconds)
            Log.d("Elijah", "removing user")
            repository.removeInactiveUser(currentSession)
        }
    }
}