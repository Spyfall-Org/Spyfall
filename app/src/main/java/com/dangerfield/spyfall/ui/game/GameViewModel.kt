package com.dangerfield.spyfall.ui.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.models.CurrentSession
import java.util.*
import java.util.concurrent.TimeUnit

class GameViewModel(private val repository: GameRepository, val currentSession: CurrentSession) :
    ViewModel() {

    private val liveGame = repository.getLiveGame()
    private val sessionEnded = repository.getSessionEnded()
    private var gameTimer: CountDownTimer? = null
    private var timerText = MutableLiveData<String>()

    fun getLiveGame() = liveGame

    fun getSessionEnded() = sessionEnded

    fun getTimeLeft(): MutableLiveData<String> {
        if (gameTimer == null) startTimer()
        return timerText
    }

    fun playAgainWasTriggered() = !currentSession.isBeingStarted()

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

    fun incrementAndroidPlayers() {
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

    companion object {
        const val timeOver = "0:00"
    }
}