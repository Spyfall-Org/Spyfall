package com.dangerfield.spyfall.ui.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.models.Session
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
enum class StartGameError(val resId: Int) {
    Unknown(R.string.start_game_unknown_error),
    GAME_STARTED(R.string.change_name_error_started_game),
}
class GameViewModel(private val repository: GameRepository, val currentSession: Session) :
    ViewModel(), LifecycleObserver {

    private val liveGame = repository.getLiveGame(currentSession)
    private val sessionEnded = repository.getSessionEnded()
    private var gameTimer: CountDownTimer? = null
    private var timerText = MutableLiveData<String>()

    fun getLiveGame() = liveGame

    fun getSessionEnded() = sessionEnded

    fun getLeaveGameEvent() = repository.getLeaveGameEvent()

    fun getRemoveInactiveUserEvent() = repository.getRemoveInactiveUserEvent()

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
            gameTimer = object : CountDownTimer(getMillisecondsFromMin(time), 1000) {
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

    //0 is special to debug mode to get a 10 seconds game
    private fun getMillisecondsFromMin(time: Long) = if(time == 0L) 10000 else (60000 * time)

    fun reassignRoles() = repository.reassignRoles(currentSession)

    companion object {
        const val timeOver = "0:00"
    }
}