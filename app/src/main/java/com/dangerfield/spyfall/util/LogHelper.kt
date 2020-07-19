package com.dangerfield.spyfall.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.models.Session
import java.lang.Exception

class LogHelper {
    companion object {
        fun logErrorCreatingGame(it: Exception) {
            Crashlytics.log("ERROR CREATING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR CREATING GAME.\n exception: ${it.localizedMessage}")
        }

        fun logErrorGettingPacksDetails(it: Exception) {
            Crashlytics.log("ERROR GETTING PACK DETAILS.\n exception: ${it.localizedMessage}")
        }

        fun logErrorJoiningGame(it: Exception) {
            Crashlytics.log("ERROR JOINING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR JOINING GAME.\n exception: ${it.localizedMessage}")
        }

        fun logNavigatingToGameScreen(currentSession: Session) {
            Crashlytics.log("User Navigating from waiting to game,\nCurrent Session: $currentSession")
        }

        fun logSessionEndedInWaiting(currentSession: Session) {
            Crashlytics.log("Session ended in waiting screen.\nCurrent Session: $currentSession")
        }

        fun logSuccesfulNameChange(currentSession: Session) {
            Crashlytics.log("Successful name change.\nCurrent Session: $currentSession")
        }

        fun logNameChangeError(it: Exception) {
            Crashlytics.log("ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")
        }

        fun logUserChangingName(newName: String, currentSession: Session) {
            Crashlytics.log("User attempting to change name\nCurrent Session: $currentSession\nNew Name: $newName")
        }

        fun logUserClickedStartGame(currentSession: Session) {
            Crashlytics.log("User clicked start game\nCurrent Session: $currentSession")
        }

        fun logUserClickedToLeaveGame(currentSession: Session) {
            Crashlytics.log("User clicked leave game\nCurrent Session: $currentSession")
        }

        fun logPlayAgainTriggered(currentSession: Session) {
            Crashlytics.log("Play again was triggered in game screen\nCurrent Session: $currentSession")
        }

        fun logSessionEndedInGame(currentSession: Session) {
            Crashlytics.log("Session was ended in game screen\nCurrent Session: $currentSession")
        }

        fun logUserClickedPlayAgain(currentSession: Session) {
            Crashlytics.log("User clicked play again\nCurrent Session: $currentSession")
        }

        fun logUserResumedGameAfterPlayAgainTriggered(currentSession: Session) {
            Crashlytics.log("User resumed game after play again had been triggered. Navigating to waiting screen.\nCurrent Session: $currentSession")
        }

        fun logUserTiggeredEndGame(currentSession: Session) {
            Crashlytics.log("User triggered end game\nCurrent Session: $currentSession")
        }

        fun logEndingGame(currentSession: Session) {
            Crashlytics.log("Game ending,\nCurrent Session: $currentSession")
        }

        fun logErrorFindingCurrentPlayerInGame(currentSession: Session) {
            Crashlytics.log("Error finding current user in the game.\nCurrent Session: $currentSession")
        }

        fun logLeaveGameError(it: Exception) {
            Crashlytics.log("ERROR LEAVING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR LEAVING GAME.\n exception: ${it.localizedMessage}")
        }

        fun logErrorWhenCheckingIfUserisAlreadyInGame() {
            Crashlytics.log("Error when checking if current user is in already in game")
        }

        fun removedInactiveUser(currentSession: Session) {
            Crashlytics.log("Removing Inactive User.\nCurrent Session: $currentSession")
            Log.d("Elijah", "Removing Inactive User.\n Current Session: ${currentSession}")
        }
    }
}