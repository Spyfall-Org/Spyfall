package com.dangerfield.spyfall.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
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
        gameRef.addSnapshotListener { game, error ->
            if (error != null) {
                Log.w("View Model", "Listen failed.", error)
                return@addSnapshotListener
            }
            if (game != null && game.exists()) {
                if (game["isStarted"] == true) { gameHasStarted.value = true }
                playerNames.value = game["playerList"] as ArrayList<String>
            }else {
                Log.d("View Model", "Current data: null")
            }
        }
        return playerNames
    }

}
