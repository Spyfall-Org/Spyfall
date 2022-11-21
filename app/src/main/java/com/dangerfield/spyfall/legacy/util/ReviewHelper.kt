package com.dangerfield.spyfall.legacy.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.dangerfield.spyfall.R

class ReviewHelper(val context: Context){

    private val promptingFrequency = 10

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
        return incrementTimesVisitedStartScreen() % promptingFrequency == 0 && !userHasClickedToReview()
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
        return newVal
    }

    private fun getGamesPlayed() =
        preferences.getInt(context.resources.getString(R.string.shared_preferences_games), 0)


    private fun userHasClickedToReview() =
        preferences.getBoolean(
            context.resources.getString(R.string.shared_preferences_hasReviewed),
            false
        )

    fun openStoreForReview() {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        try {
            startActivity( context, goToMarket, Bundle())
        } catch (e: ActivityNotFoundException) {
            startActivity(
                context,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                ), Bundle()
            )
        }
    }
}