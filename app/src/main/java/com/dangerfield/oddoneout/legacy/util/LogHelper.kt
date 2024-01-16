package com.dangerfield.oddoneout.legacy.util

import android.util.Log
import com.dangerfield.oddoneout.legacy.models.Session
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
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logSessionEndedInWaiting(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logSuccesfulNameChange(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logNameChangeError(it: Exception) {
            //  Crashlytics.log("ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")
        }

        fun logUserChangingName(newName: String, currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logUserClickedStartGame(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logUserClickedToLeaveGame(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logPlayAgainTriggered(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logSessionEndedInGame(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logUserClickedPlayAgain(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logUserTiggeredEndGame(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logEndingGame(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logErrorFindingCurrentPlayerInGame(currentSession: Session) {
            Log.d("Elijah", "logNavigatingToGameScreen $currentSession")
        }

        fun logLeaveGameError(it: Exception) {
            // Crashlytics.log("ERROR LEAVING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "logNavigatingToGameScreen $it")
        }

        fun logErrorWhenCheckingIfUserisAlreadyInGame() {
            Log.d("Elijah", "logNavigatingToGameScreen")
        }

        fun removedInactiveUser(currentSession: Session) {
            //  Crashlytics.log("Removing Inactive User.\nCurrent spyfallx.coregameapi.Session: $currentSession")
            Log.d("Elijah", "Removing Inactive User.\n Current spyfallx.coregameapi.Session: $currentSession")
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
