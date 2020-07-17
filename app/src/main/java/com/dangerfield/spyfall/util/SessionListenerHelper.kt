package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.SessionListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class SessionListenerHelper(
    private val constants: Constants,
    private val db: FirebaseFirestore
) {
    private var gameListener: ListenerRegistration? = null

    fun isListening() = gameListener != null

    fun removeListener() {
        gameListener?.remove()
        gameListener = null
    }

    fun addListener(
        sessionListener: SessionListener,
        session: CurrentSession
    ) {
        val gameRef: DocumentReference = db.collection(constants.games).document(session.accessCode)
        gameListener = gameRef.addSnapshotListener { result, error ->
            if (error != null) return@addSnapshotListener

            if (result != null && result.exists()) {
                val updatedGame = result.toObject(Game::class.java) ?: return@addSnapshotListener
                val noUsersInGame = updatedGame.playerList.size == 0
                if (noUsersInGame) {
                    gameRef.delete() // will cause listener to endSession() when updated
                } else {
                    updateSession(updatedGame, sessionListener)
                }
            } else {
                endSession(sessionListener)
            }
        }
    }

    private fun endSession(sessionListener: SessionListener) {
        removeListener()
        sessionListener.onSessionEnded()
    }

    private fun updateSession(game: Game, sessionListener: SessionListener) {
        sessionListener.onGameUpdates(game)
    }
}