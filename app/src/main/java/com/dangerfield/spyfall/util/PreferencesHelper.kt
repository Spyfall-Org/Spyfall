package com.dangerfield.spyfall.util

import android.content.Context
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.models.Game

interface TesterPreferences {
    fun getUseTestDbState(): Boolean
    fun setUseTestDbState(useTest: Boolean)
}

interface ColorPreferences {

}

interface SessionSaver {
    fun saveSession(currentSession: CurrentSession)
    fun removeSavedSession(currentSession: CurrentSession)
    fun getSavedSession() : CurrentSession?
}
class PreferencesHelper(val context: Context) : TesterPreferences, ColorPreferences , SessionSaver {

    private val preferences by lazy {
        context.getSharedPreferences(
            context.resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
    }

    override fun getUseTestDbState(): Boolean {
        return preferences.getBoolean(
            context.resources.getString(R.string.shared_preferences_test_db),
            true
        )
    }

    override fun setUseTestDbState(useTest: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(context.resources.getString(R.string.shared_preferences_test_db), useTest)
        editor.apply()
    }

    override fun saveSession(currentSession: CurrentSession) {
        val editor = preferences.edit()
        editor.putString(context.resources.getString(R.string.shared_preferences_session_code), currentSession.accessCode)
        editor.putString(context.resources.getString(R.string.shared_preferences_session_current_user), currentSession.currentUser)
        editor.apply()
    }

    override fun removeSavedSession(currentSession: CurrentSession) {
        val editor = preferences.edit()
        editor.remove(context.resources.getString(R.string.shared_preferences_session_code))
        editor.remove(context.resources.getString(R.string.shared_preferences_session_current_user))
        editor.apply()
    }

    override fun getSavedSession(): CurrentSession? {
        val accessCode = preferences.getString(
            context.resources.getString(R.string.shared_preferences_session_code),
            null
        ) ?: return null

        val currentUser =  preferences.getString(
            context.resources.getString(R.string.shared_preferences_session_current_user),
            null
        ) ?: return null

        return CurrentSession(accessCode = accessCode, currentUser = currentUser, game = Game.getEmptyGame())
    }
}