package com.dangerfield.spyfall.legacy.util

import android.util.Log
import com.dangerfield.spyfall.legacy.models.Session
import java.lang.Exception

class LogHelper {
    companion object {
        fun logErrorCreatingGame(it: Exception) {
           // Crashlytics.log("ERROR CREATING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR CREATING GAME.\n exception: ${it.localizedMessage}")
        }

        fun logErrorGettingPacksDetails(it: Exception) {
           // Crashlytics.log("ERROR GETTING PACK DETAILS.\n exception: ${it.localizedMessage}")
        }

        fun logErrorJoiningGame(it: Exception) {
          //  Crashlytics.log("ERROR JOINING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR JOINING GAME.\n exception: ${it.localizedMessage}")
        }

        fun logNavigatingToGameScreen(currentSession: Session) {
          //  Crashlytics.log("User Navigating from waiting to game,\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logSessionEndedInWaiting(currentSession: Session) {
         //   Crashlytics.log("spyfallx.coregameapi.Session ended in waiting screen.\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logSuccesfulNameChange(currentSession: Session) {
          //  Crashlytics.log("Successful name change.\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logNameChangeError(it: Exception) {
          //  Crashlytics.log("ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")
        }

        fun logUserChangingName(newName: String, currentSession: Session) {
           // Crashlytics.log("User attempting to change name\nCurrent spyfallx.coregameapi.Session: $currentSession\nNew Name: $newName")
        }

        fun logUserClickedStartGame(currentSession: Session) {
           // Crashlytics.log("User clicked start game\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logUserClickedToLeaveGame(currentSession: Session) {
           // Crashlytics.log("User clicked leave game\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logPlayAgainTriggered(currentSession: Session) {
           // Crashlytics.log("Play again was triggered in game screen\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logSessionEndedInGame(currentSession: Session) {
           // Crashlytics.log("spyfallx.coregameapi.Session was ended in game screen\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logUserClickedPlayAgain(currentSession: Session) {
           // Crashlytics.log("User clicked play again\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logUserTiggeredEndGame(currentSession: Session) {
            //Crashlytics.log("User triggered end game\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logEndingGame(currentSession: Session) {
           // Crashlytics.log("Game ending,\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logErrorFindingCurrentPlayerInGame(currentSession: Session) {
           // Crashlytics.log("Error finding current user in the game.\nCurrent spyfallx.coregameapi.Session: $currentSession")
        }

        fun logLeaveGameError(it: Exception) {
           // Crashlytics.log("ERROR LEAVING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR LEAVING GAME.\n exception: ${it.localizedMessage}")
        }

        fun logErrorWhenCheckingIfUserisAlreadyInGame() {
           // Crashlytics.log("Error when checking if current user is in already in game")
        }

        fun removedInactiveUser(currentSession: Session) {
          //  Crashlytics.log("Removing Inactive User.\nCurrent spyfallx.coregameapi.Session: $currentSession")
            Log.d("Elijah", "Removing Inactive User.\n Current spyfallx.coregameapi.Session: ${currentSession}")
        }

        fun logStartGameError(it: Exception) {
          //  Crashlytics.log("ERROR STARTING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR STARTING GAME.\n exception: ${it.localizedMessage}")
        }

        fun logErrorPlayAgain(it: Exception) {
          //  Crashlytics.log("ERROR PLAY AGAIN GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR PLAY AGAIN GAME.\n exception: ${it.localizedMessage}")
        }
    }
}