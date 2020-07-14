package com.dangerfield.spyfall.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.api.GameRepository
import java.util.*
import java.util.concurrent.TimeUnit

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val liveGame = repository.currentSession?.getLiveGame()
    val gameExists = repository.currentSession?.getGameExists()
    var timerText = MutableLiveData<String>()
    private var gameTimer: CountDownTimer? = null


    fun getTimeLeft(): MutableLiveData<String> {
        if(gameTimer == null) startTimer()
        return timerText
    }

    private fun startTimer(){
        liveGame?.value?.timeLimit?.let {time ->
            gameTimer = object : CountDownTimer((60000*time), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val text = String.format(
                        Locale.getDefault(), "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                    )
                    Log.d("Timer", text)
                    timerText.postValue(text)
                }

                override fun onFinish() { timerText.postValue(timeOver) }
            }.start()
        }
    }

    fun playAgainWasTriggered() = repository.currentSession?.isBeingStarted() == false

    fun resetTimer(){
        gameTimer?.cancel()
        gameTimer = null
    }

    fun resetGame() {
        resetTimer()
        repository.resetGame()
    }

    fun getCurrentUser() = repository.currentSession?.currentUser

    fun triggerEndGame() { repository.endGame() }

    fun incrementAndroidPlayers(){
        repository.incrementAndroidPlayers()
    }

    companion object {
        const val timeOver = "0:00"
    }
}