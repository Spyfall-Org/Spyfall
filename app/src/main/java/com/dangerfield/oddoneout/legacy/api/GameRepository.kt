package com.dangerfield.oddoneout.legacy.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.oddoneout.legacy.models.Game
import com.dangerfield.oddoneout.legacy.models.GamePack
import com.dangerfield.oddoneout.legacy.models.Session
import com.dangerfield.oddoneout.legacy.ui.game.PlayAgainError
import com.dangerfield.oddoneout.legacy.ui.game.StartGameError
import com.dangerfield.oddoneout.legacy.ui.joinGame.JoinGameError
import com.dangerfield.oddoneout.legacy.ui.newGame.NewGameError
import com.dangerfield.oddoneout.legacy.ui.newGame.PackDetailsError
import com.dangerfield.oddoneout.legacy.ui.waiting.LeaveGameError
import com.dangerfield.oddoneout.legacy.ui.waiting.NameChangeError
import com.dangerfield.oddoneout.legacy.util.Event
import java.lang.Exception

interface GameRepository {
    fun createGame(
        username: String,
        timeLimit: Long,
        chosenPacks: List<String>
    ): LiveData<Resource<Session, NewGameError>>

    fun joinGame(
        accessCode: String,
        username: String
    ): LiveData<Event<Resource<Session, JoinGameError>>>

    fun leaveGame(currentSession: Session)
    fun endGame(currentSession: Session): MutableLiveData<Resource<Unit, Exception>>
    fun startGame(currentSession: Session): MutableLiveData<Event<Resource<Unit, StartGameError>>>
    fun resetGame(currentSession: Session): MutableLiveData<Resource<Unit, PlayAgainError>>
    fun changeName(
        newName: String,
        currentSession: Session
    ): LiveData<Event<Resource<String, NameChangeError>>>

    fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>>
    fun incrementAndroidPlayers()
    fun incrementGamesPlayed()
    fun getPacks(): ArrayList<GamePack>
    fun getLiveGame(currentSession: Session): MutableLiveData<Game>
    fun getSessionEnded(): MutableLiveData<Event<Unit>>
    fun getRemoveInactiveUserEvent(): MutableLiveData<Event<Resource<Unit, Unit>>>
    fun getLeaveGameEvent(): MutableLiveData<Event<Resource<Unit, LeaveGameError>>>
    fun removeInactiveUser(currentSession: Session)
    fun reassignRoles(currentSession: Session): MutableLiveData<Event<Resource<Unit, StartGameError>>>
    fun cancelJobs(): Unit?
    fun cancelCreateGame(): Unit?
    fun cancelJoinGame(): Unit?
    fun cancelChangeName(): Unit?
    fun cancelStartGame(): Unit?
}
