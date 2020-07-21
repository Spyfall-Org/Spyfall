package com.dangerfield.spyfall.util

import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.models.Game
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SavedSessionHelper(
    private val preferencesHelper: PreferencesHelper,
    private val db: FirebaseFirestore,
    private val constants: Constants
) {
    /**
     * Checks preferences for a saved game
     * if that game is still on firebase we assume the user can still join it
     */
    suspend fun whenUserIsInExistingGame(navigateToGame: (Session, Boolean) -> Unit) {
        preferencesHelper.getSavedSession()?.let { session ->
            try {
                val result =
                    db.collection(constants.games).document(session.accessCode).get().await()
                if (result.exists()) {
                    val updatedGame = result.toObject(Game::class.java) ?: return
                    if (userCanEnterGame(updatedGame, session)) {
                        navigateToGame.invoke(session, updatedGame.started)
                    }
                }
            } catch (e: Exception) {
                LogHelper.logErrorWhenCheckingIfUserisAlreadyInGame()
            }
        }
    }

    private fun userCanEnterGame(game: Game, currentSession: Session): Boolean {
        return (!gameIsExpired(game)
                && (game.playerList.contains(currentSession.currentUser) || game.playerList.contains(currentSession.previousUserName))
                && (!gameIsStarted(game) || userCanEnterStartedGame(game, currentSession))
                )
    }

    private fun userCanEnterStartedGame(game: Game, currentSession: Session): Boolean {
        return (
                !gameIsExpired(game)
                        && (game.playerObjectList.find { it.username == currentSession.currentUser } != null
                        || game.playerObjectList.find { it.username == currentSession.previousUserName } != null)
                )
    }

    private fun gameIsExpired(game: Game): Boolean {
        game.expiration.let { expiration ->
            val now = System.currentTimeMillis() / 1000
            return expiration <= now
        }
    }

    private fun gameIsStarted(game: Game) = game.playerObjectList.size > 0
}