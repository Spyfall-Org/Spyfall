package com.dangerfield.spyfall.util

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dangerfield.spyfall.api.GameRepository
import kotlinx.coroutines.*

class RemoveUserTimer(val repository: GameRepository) : LifecycleObserver {

    private var killGame: Job = Job()
    private val fifteenMins = 900000L

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        //stopTimerToRemoveUser()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        //startTimerToRemoveUser()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyed() {
        //stopTimerToRemoveUser()
        //repository.leaveGame()
    }

    private fun stopTimerToRemoveUser() {
        if (killGame.isActive) {
            try {
                killGame.cancel()
            } catch (e: CancellationException) {
                Log.d("Eli", "Killing of the game was cancelled")
            }
        }
    }

    private fun startTimerToRemoveUser() {
        killGame = GlobalScope.launch {
            delay(fifteenMins)
            //repository.leaveGame()
        }
    }
}