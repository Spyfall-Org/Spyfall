package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.spyfall.joinGame.JoinGameError
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.newGame.NewGameError
import com.dangerfield.spyfall.newGame.PackDetailsError
import com.dangerfield.spyfall.util.Event
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
    abstract fun changeName()
    abstract fun getPacksDetails(): LiveData<Resource<List<List<String>>, PackDetailsError>>
}