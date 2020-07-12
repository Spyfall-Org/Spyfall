package com.dangerfield.spyfall.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration

class CurrentSession (
    val accessCode: String,
    val currentUser: String,
    val liveGame : MutableLiveData<Game> = MutableLiveData(),
    val gameExists: MutableLiveData<Boolean> = MutableLiveData(),
    var gameListener : ListenerRegistration? = null
) {

    fun hasStartedGame() = liveGame.value?.started == true

    fun withListener(gameRef: DocumentReference): CurrentSession {
        gameListener = gameRef.addSnapshotListener { result, error ->
            if (error != null)  return@addSnapshotListener
            if (result != null && result.exists()) {
                val updatedGame = result.toObject(Game::class.java)
                if(updatedGame?.playerList?.size == 0) {
                    gameRef.delete()
                }else {
                    liveGame.value = updatedGame
                    gameExists.value = true
                }
            }else {
                gameExists.value = false
            }
        }
        return this
    }

    fun endSession() {
        gameExists.value = false
        gameListener?.remove()
    }
}
