package com.dangerfield.spyfall.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.models.CurrentSession
import java.lang.Exception

class CrashlyticsLogger {
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

        fun logNavigatingToGameScreen(currentSession: CurrentSession) {
            Crashlytics.log("User Navigating from waiting to game,\nCurrent Session: $currentSession")
        }

        fun logSessionEndedInWaiting(currentSession: CurrentSession) {
            Crashlytics.log("Session ended in waiting screen.\nCurrent Session: $currentSession")
        }

        fun logSuccesfulNameChange(currentSession: CurrentSession) {
            Crashlytics.log("Successful name change.\nCurrent Session: $currentSession")
        }

        fun logNameChangeError(it: Exception) {
            Crashlytics.log("ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR CHANGING NAME.\n exception: ${it.localizedMessage}")

        }

        fun logUserChangingName(newName: String, currentSession: CurrentSession) {
            Crashlytics.log("User attempting to change name\nCurrent Session: $currentSession\nNew Name: $newName")
        }

        fun logUserClickedStartGame(currentSession: CurrentSession) {
            Crashlytics.log("User clicked start game\nCurrent Session: $currentSession")
        }

        fun logUserClickedToLeaveGame(currentSession: CurrentSession) {
            Crashlytics.log("User clicked leave game\nCurrent Session: $currentSession")
        }

        fun logPlayAgainTriggered(currentSession: CurrentSession) {
            Crashlytics.log("Play again was triggered in game screen\nCurrent Session: $currentSession")
        }

        fun logSessionEndedInGame(currentSession: CurrentSession) {
            Crashlytics.log("Session was ended in game screen\nCurrent Session: $currentSession")
        }

        fun logUserClickedPlayAgain(currentSession: CurrentSession) {
            Crashlytics.log("User clicked play again\nCurrent Session: $currentSession")
        }

        fun logUserResumedGameAfterPlayAgainTriggered(currentSession: CurrentSession) {
            Crashlytics.log("User resumed game after play again had been triggered. Navigating to waiting screen.\nCurrent Session: $currentSession")
        }

        fun logUserTiggeredEndGame(currentSession: CurrentSession) {
            Crashlytics.log("User triggered end game\nCurrent Session: $currentSession")
        }

        fun logEndingGame(currentSession: CurrentSession) {
            Crashlytics.log("Game ending,\nCurrent Session: $currentSession")
        }

        fun logErrorFindingCurrentPlayerInGame(currentSession: CurrentSession) {
            Crashlytics.log("Error finding current user in the game.\nCurrent Session: $currentSession")
        }

        fun logLeaveGameError(it: Exception) {
            Crashlytics.log("ERROR LEAVING GAME.\n exception: ${it.localizedMessage}")
            Log.d("Elijah", "ERROR LEAVING GAME.\n exception: ${it.localizedMessage}")

        }

        fun logErrorWhenCheckingIfUserisAlreadyInGame() {
            Crashlytics.log("Error when checking if current user is in already in game")
        }
    }
}