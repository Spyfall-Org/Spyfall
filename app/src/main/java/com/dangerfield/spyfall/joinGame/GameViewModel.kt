package com.dangerfield.spyfall.joinGame

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.dangerfield.spyfall.WaitingActivity.WaitingGame
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class GameViewModel : ViewModel() {

    private var playerNames: MutableLiveData<ArrayList<String>> = MutableLiveData()
    var gameHasStarted: MutableLiveData<Boolean> = MutableLiveData()
    var db = FirebaseFirestore.getInstance()
    var ACCESS_CODE =   UUID.randomUUID().toString().substring(0,6).toLowerCase()

    //so I want playerNames to be listening to firebases playerlist
    fun getPlayerNames(): LiveData<ArrayList<String>>  {
        val gameRef = db.collection("games").document(ACCESS_CODE)
        gameRef.addSnapshotListener { game, e ->
            if (e != null) {
                Log.w("View Model", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (game != null && game.exists()) {
                if (game["isStarted"] == true) {
                    gameHasStarted.value = true
                }
                Log.d("View Model", "Current game data: ${game.data}")
                playerNames.value = game["playerList"] as ArrayList<String>
                Log.d("View Model", "Game[playerList] = ${game["playerList"]}")
            } else {
                Log.d("View Model", "Current data: null")
            }
        }
        return playerNames
    }



}
