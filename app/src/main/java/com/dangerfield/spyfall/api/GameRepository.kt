package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.spyfall.ui.joinGame.JoinGameError
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.GamePack
import com.dangerfield.spyfall.ui.newGame.NewGameError
import com.dangerfield.spyfall.ui.newGame.PackDetailsError
import com.dangerfield.spyfall.util.Event
import com.dangerfield.spyfall.ui.waiting.NameChangeError

interface GameRepository {
    fun createGame(
        username: String,
        timeLimit: Long,
        chosenPacks: List<String>
    ): LiveData<Resource<CurrentSession, NewGameError>>

    fun joinGame(
        accessCode: String,
        username: String
    ): LiveData<Resource<CurrentSession, JoinGameError>>

    fun leaveGame(currentSession: CurrentSession)
    fun endGame(currentSession: CurrentSession)
    fun startGame(currentSession: CurrentSession)
    fun resetGame(currentSession: CurrentSession)
    fun changeName(
        newName: String,
        currentSession: CurrentSession
    ): LiveData<Event<Resource<String, NameChangeError>>>

    fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>>
    fun incrementAndroidPlayers()
    fun incrementGamesPlayed()
    fun getPacks(): ArrayList<GamePack>
    fun getLiveGame(): MutableLiveData<Game>
    fun getSessionEnded(): MutableLiveData<Event<Unit>>
}