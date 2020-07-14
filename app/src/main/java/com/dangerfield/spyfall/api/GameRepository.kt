package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dangerfield.spyfall.joinGame.JoinGameError
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.GamePack
import com.dangerfield.spyfall.newGame.NewGameError
import com.dangerfield.spyfall.newGame.PackDetailsError
import com.dangerfield.spyfall.util.Event
import com.dangerfield.spyfall.waiting.NameChangeError
import com.google.firebase.firestore.FirebaseFirestore

abstract class GameRepository {

    abstract var currentSession: CurrentSession?
    abstract var db : FirebaseFirestore

    abstract fun createGame(username: String, timeLimit: Long, chosenPacks: List<String>): LiveData<Resource<Unit, NewGameError>>
    abstract fun joinGame(accessCode: String, username:String): LiveData<Resource<Unit, JoinGameError>>
    abstract fun leaveGame()
    abstract fun endGame()
    abstract fun startGame()
    abstract fun resetGame()
    abstract fun changeName(newName: String) :  LiveData<Event<Resource<String, NameChangeError>>>
    abstract fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>>
    abstract fun incrementAndroidPlayers()
    abstract fun incrementGamesPlayed()
    abstract fun getPacks(): ArrayList<GamePack>
}