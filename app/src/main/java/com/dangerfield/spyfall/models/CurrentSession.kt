package com.dangerfield.spyfall.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration

interface SessionEndListener {
    fun onSessionEnded()
}
class CurrentSession (
    val accessCode: String,
    var currentUser: String,
    private val liveGame : MutableLiveData<Game> = MutableLiveData(),
    private val gameExists: MutableLiveData<Boolean> = MutableLiveData(),
    private var gameListener : ListenerRegistration? = null,
    private var sessionEndListener: SessionEndListener
) {

    fun isBeingStarted() = liveGame.value?.started == true

    fun gameHasBegun() = liveGame.value?.playerObjectList?.isNotEmpty() ?: false

    fun getGameValue() = liveGame.value

    fun withListener(gameRef: DocumentReference): CurrentSession {
        gameListener = gameRef.addSnapshotListener { result, error ->
            if (error != null)  return@addSnapshotListener

            if (result != null && result.exists()) {
                val updatedGame = result.toObject(Game::class.java) ?: return@addSnapshotListener
                val noUsersInGame = updatedGame.playerList.size == 0
                val currentUserWasRemoved =  !updatedGame.playerList.contains(currentUser)
                when {
                    noUsersInGame -> gameRef.delete()
                    currentUserWasRemoved -> endSession()
                    else -> {
                        liveGame.value = updatedGame
                        gameExists.value = true
                    }
                }
            }else {
                endSession()
            }
        }
        return this
    }

    fun getLiveGame() = liveGame
    fun getGameExists() = gameExists

    private fun endSession() {
        gameListener?.remove()
        gameExists.postValue(false)
        sessionEndListener.onSessionEnded()
    }
}
