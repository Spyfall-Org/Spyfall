package com.dangerfield.spyfall.ui.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.models.Session
import java.util.*
import java.util.concurrent.TimeUnit

class GameViewModel(private val repository: GameRepository, val currentSession: Session) :
    ViewModel(), LifecycleObserver {

    private val liveGame = repository.getLiveGame(currentSession)
    private val sessionEnded = repository.getSessionEnded()
    private var gameTimer: CountDownTimer? = null
    private var timerText = MutableLiveData<String>()

    fun getLiveGame() = liveGame

    fun getSessionEnded() = sessionEnded

    fun getLeaveGameEvent() = repository.getLeaveGameEvent()

    fun getTimeLeft(): MutableLiveData<String> {
        if (gameTimer == null) startTimer()
        return timerText
    }

    fun playAgainWasTriggered() = !currentSession.game.started

    fun stopTimer() {
        gameTimer?.cancel()
        gameTimer = null
    }

    fun resetGame() {
        stopTimer()
        repository.resetGame(currentSession)
    }

    fun triggerEndGame() {
        repository.endGame(currentSession)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun incrementAndroidPlayers() {
        repository.incrementAndroidPlayers()
    }

    private fun startTimer() {
        currentSession.game.timeLimit.let { time ->
            gameTimer = object : CountDownTimer((60000 * time), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val text = String.format(
                        Locale.getDefault(), "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                    )
                    Log.d("Timer", text)
                    timerText.postValue(text)
                }

                override fun onFinish() {
                    timerText.postValue(timeOver)
                }
            }.start()
        }
    }

    fun forceRefreshGame() { liveGame.postValue(liveGame.value) }

    companion object {
        const val timeOver = "0:00"
    }
}