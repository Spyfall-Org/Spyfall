package com.dangerfield.spyfall.ui.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.util.Event
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
enum class StartGameError(val resId: Int) {
    Unknown(R.string.start_game_unknown_error),
    GAME_STARTED(R.string.change_name_error_started_game),
}

enum class PlayAgainError(val resId: Int) {
    Unknown(R.string.play_again_unknown_error),
}
class GameViewModel(private val repository: GameRepository, val currentSession: Session) :
    ViewModel(), LifecycleObserver {

    private var gameTimer: CountDownTimer? = null

    //Globally triggered events
    private val liveGame = repository.getLiveGame(currentSession)
    private val sessionEndedEvent = repository.getSessionEnded()
    private val removeInactiveUserEvent = repository.getRemoveInactiveUserEvent()
    private val leaveGameEvent = repository.getLeaveGameEvent()

    //Locally triggered events
    private val reassignEvent = MediatorLiveData<Event<Resource<Unit, StartGameError>>>()
    private val playAgainEvent = MediatorLiveData<Event<Resource<Unit, PlayAgainError>>>()
    private val currentUserEndedGameEvent = MediatorLiveData<Event<Resource<Unit, Exception>>>()
    private var timerText = MutableLiveData<String>()

    fun getLiveGame() = liveGame

    fun getSessionEnded() = sessionEndedEvent

    fun getLeaveGameEvent() = leaveGameEvent

    fun getRemoveInactiveUserEvent() = removeInactiveUserEvent

    fun getReassignEvent() = reassignEvent

    fun getPlayAgainEvent() = playAgainEvent

    fun getCurrentUserEndedGameEvent() = currentUserEndedGameEvent

    fun getTimeLeft(): MutableLiveData<String> {
        if (gameTimer == null) startTimer()
        return timerText
    }

    fun triggerPlayAgain() {
        val repoResult = repository.resetGame(currentSession)
        playAgainEvent.addSource(repoResult) {
            playAgainEvent.postValue(Event(it))
            playAgainEvent.removeSource(repoResult)
        }
    }

    fun triggerEndGame() {
        val repoResult = repository.endGame(currentSession)
        currentUserEndedGameEvent.addSource(repoResult) {
            currentUserEndedGameEvent.postValue(Event(it))
            currentUserEndedGameEvent.removeSource(repoResult)
        }
    }

    fun triggerReassignRoles() {
        val repoResult = repository.reassignRoles(currentSession)
        reassignEvent.addSource(repoResult) {
            repoResult.postValue(it)
            reassignEvent.removeSource(repoResult)
        }
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

    fun stopTimer() {
        gameTimer?.cancel()
        gameTimer = null
    }

    //0 is special to debug mode to get a 10 seconds game
    private fun getMillisecondsFromMin(time: Long) = if(time == 0L) 10000 else (60000 * time)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun incrementAndroidPlayers() {
        repository.incrementAndroidPlayers()
    }

    companion object {
        const val timeOver = "0:00"
    }
}