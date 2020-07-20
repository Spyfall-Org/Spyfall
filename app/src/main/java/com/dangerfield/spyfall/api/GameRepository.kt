package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.spyfall.ui.joinGame.JoinGameError
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.GamePack
import com.dangerfield.spyfall.ui.game.PlayAgainError
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.ui.newGame.NewGameError
import com.dangerfield.spyfall.ui.newGame.PackDetailsError
import com.dangerfield.spyfall.ui.waiting.LeaveGameError
import com.dangerfield.spyfall.util.Event
import com.dangerfield.spyfall.ui.waiting.NameChangeError
import com.google.android.gms.tasks.Task
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
    fun resetGame(currentSession: Session):  MutableLiveData<Resource<Unit, PlayAgainError>>
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
    fun cancelJobs()
    fun getLeaveGameEvent(): MutableLiveData<Event<Resource<Unit, LeaveGameError>>>
    fun getRemoveInactiveUserEvent(): MutableLiveData<Event<Resource<Unit, Unit>>>
    fun removeInactiveUser(currentSession: Session)
    fun reassignRoles(currentSession: Session) : MutableLiveData<Event<Resource<Unit, StartGameError>>>
}