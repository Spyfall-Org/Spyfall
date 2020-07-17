package com.dangerfield.spyfall.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.models.CurrentSession
import kotlinx.coroutines.*

class RemoveUserTimer(val repository: GameRepository, val preferencesHelper: PreferencesHelper) : LifecycleObserver {

    private var killGame: Job = Job()
    private val fifteenMins = 900000L
    private val currentSession : CurrentSession?
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
            repository.leaveGame(it)
        }
    }

    private fun stopTimerToRemoveUser() {
        if (killGame.isActive) {
            try {
                killGame.cancel()
            } catch (e: CancellationException) { }
        }
    }

    private fun startTimerToRemoveUser(currentSession: CurrentSession) {
        killGame = GlobalScope.launch {
            delay(fifteenMins)
            repository.leaveGame(currentSession)
        }
    }
}