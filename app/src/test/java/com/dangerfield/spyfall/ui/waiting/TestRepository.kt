package com.dangerfield.spyfall.ui.waiting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.GamePack
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.game.PlayAgainError
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.ui.joinGame.JoinGameError
import com.dangerfield.spyfall.ui.newGame.NewGameError
import com.dangerfield.spyfall.ui.newGame.PackDetailsError
import com.dangerfield.spyfall.util.Event
import java.lang.Exception

class TestRepository : GameRepository {

    override fun createGame(
        username: String,
        timeLimit: Long,
        chosenPacks: List<String>
    ): LiveData<Resource<Session, NewGameError>> {
        TODO("Not yet implemented")
    }

    override fun joinGame(
        accessCode: String,
        username: String
    ) = MutableLiveData<Event<Resource<Session, JoinGameError>>> ()

    override fun leaveGame(currentSession: Session) {
        TODO("Not yet implemented")
    }

    override fun endGame(currentSession: Session): MutableLiveData<Resource<Unit, Exception>> {
        TODO("Not yet implemented")
    }

    override fun startGame(currentSession: Session): MutableLiveData<Event<Resource<Unit, StartGameError>>> {
        TODO("Not yet implemented")
    }

    override fun resetGame(currentSession: Session): MutableLiveData<Resource<Unit, PlayAgainError>> {
        TODO("Not yet implemented")
    }

    override fun changeName(
        newName: String,
        currentSession: Session
    ): LiveData<Event<Resource<String, NameChangeError>>> {
        TODO("Not yet implemented")
    }

    override fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>> {
        TODO("Not yet implemented")
    }

    override fun incrementAndroidPlayers() {
        TODO("Not yet implemented")
    }

    override fun incrementGamesPlayed() {
        TODO("Not yet implemented")
    }

    override fun getPacks(): ArrayList<GamePack> {
        TODO("Not yet implemented")
    }

    override fun getLiveGame(currentSession: Session): MutableLiveData<Game> {
        TODO("Not yet implemented")
    }

    override fun getSessionEnded(): MutableLiveData<Event<Unit>> {
        TODO("Not yet implemented")
    }

    override fun getRemoveInactiveUserEvent(): MutableLiveData<Event<Resource<Unit, Unit>>> {
        TODO("Not yet implemented")
    }

    override fun getLeaveGameEvent(): MutableLiveData<Event<Resource<Unit, LeaveGameError>>> {
        TODO("Not yet implemented")
    }

    override fun removeInactiveUser(currentSession: Session) {
        TODO("Not yet implemented")
    }

    override fun reassignRoles(currentSession: Session): MutableLiveData<Event<Resource<Unit, StartGameError>>> {
        TODO("Not yet implemented")
    }

    override fun cancelJobs(): Unit? {
        TODO("Not yet implemented")
    }

    override fun cancelCreateGame(): Unit? {
        TODO("Not yet implemented")
    }

    override fun cancelJoinGame(): Unit? {
        TODO("Not yet implemented")
    }

    override fun cancelChangeName(): Unit? {
        TODO("Not yet implemented")
    }

    override fun cancelStartGame(): Unit? {
        TODO("Not yet implemented")
    }
}