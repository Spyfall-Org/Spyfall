package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.Game
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SavedSessionHelper(private val preferencesHelper: PreferencesHelper, private val db : FirebaseFirestore, private val constants: Constants) {
    /**
     * Checks preferences for a saved game
     * if that game is still on firebase we assume the user can still join it
     */
    suspend fun whenUserIsInExistingGame(whenTrue : (CurrentSession, Boolean) -> Unit) {
        preferencesHelper.getSavedSession()?.let {
            try {
                val result = db.collection(constants.games).document(it.accessCode).get().await()
                if(result.exists()) {
                    val updatedGame = result.toObject(Game::class.java) ?: return
                    if(updatedGame.playerList.contains(it.currentUser)) {
                        if(updatedGame.started){
                            whenTrue.invoke(it, true) // user in game that has been started

                        }else {
                            whenTrue.invoke(it, false) // user in game that has not been started
                        }
                    }
                }
            } catch (e: Exception) {
                CrashlyticsLogger.logErrorWhenCheckingIfUserisAlreadyInGame()
            }
        }
    }
}