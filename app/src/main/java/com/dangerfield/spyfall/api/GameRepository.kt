package com.dangerfield.spyfall.api

import androidx.lifecycle.LiveData
import com.dangerfield.spyfall.joinGame.JoinGameError
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.util.Event
import com.google.firebase.firestore.FirebaseFirestore

abstract class GameRepository {

    abstract var game : LiveData<Game>
    abstract var db : FirebaseFirestore

    abstract fun createGame(chosenPacks: List<String>, timeLimit: Int, username: String): LiveData<Event<String>>
    abstract fun joinGame(accessCode: String, username:String): LiveData<Resource<Unit, JoinGameError>>
    abstract fun leaveGame()
    abstract fun endGame()
    abstract fun startGame()
    abstract fun resetGame()
    abstract fun changeName()
}