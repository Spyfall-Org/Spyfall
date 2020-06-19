package com.dangerfield.spyfall.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration

class CurrentSession (
    val accessCode: String,
    val currentUser: String,
    val game : MutableLiveData<Game> = MutableLiveData(),
    val gameExists: MutableLiveData<Boolean> = MutableLiveData(),
    var gameListener : ListenerRegistration? = null
) {

    fun build(gameRef: DocumentReference): CurrentSession {
        gameListener = gameRef.addSnapshotListener { result, error ->
            if (error != null)  return@addSnapshotListener
            if (result != null && result.exists()) {
                game.value = result.toObject(Game::class.java)
                gameExists.value = true
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
