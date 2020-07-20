package com.dangerfield.spyfall.util

import android.content.Context
import android.util.Log
import com.dangerfield.spyfall.R

class ReviewHelper(val context: Context){

    private val preferences = context.getSharedPreferences(
        context.resources.getString(R.string.shared_preferences),
        Context.MODE_PRIVATE
    )

    /**
     * Returns weather or not the user should be prompter to review the application in the
     * store
     *
     * @True if the user has played a multiple of 5 games (each ends in a visit to the start screen)
     * and has not yet clicked to give a review
     */
    fun shouldPromptForReview(): Boolean {
        return incrementTimesVisitedStartScreen() % 5 == 0 && !userHasClickedToReview()
    }

    fun setHasClickedToReview() {
        val editor = preferences.edit()
        editor.putBoolean(context.resources.getString(R.string.shared_preferences_hasReviewed), true)
        editor.apply()
    }

    private fun incrementTimesVisitedStartScreen(): Int {
        val editor = preferences.edit()
        val newVal = getGamesPlayed() + 1
        editor.putInt(context.resources.getString(R.string.shared_preferences_games), newVal)
        editor.apply()
        Log.d("Elijah", "User has visited start screen for the ${newVal} time")
        return newVal
    }

    private fun getGamesPlayed() =
        preferences.getInt(context.resources.getString(R.string.shared_preferences_games), 0)


    private fun userHasClickedToReview() =
        preferences.getBoolean(
            context.resources.getString(R.string.shared_preferences_hasReviewed),
            false
        )
}