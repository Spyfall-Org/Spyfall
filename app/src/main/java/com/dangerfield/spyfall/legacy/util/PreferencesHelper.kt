package com.dangerfield.spyfall.legacy.util

import android.content.Context
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.legacy.models.Session
import com.dangerfield.spyfall.legacy.models.Game

interface TesterPreferences {
    fun getUseTestDbState(): Boolean
    fun setUseTestDbState(useTest: Boolean)
}

interface SessionSaver {
    fun saveSession(currentSession: Session)
    fun removeSavedSession()
    fun getSavedSession() : Session?
}

interface PreferencesService : TesterPreferences, SessionSaver

class PreferencesHelper(val context: Context) : PreferencesService {

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

    override fun saveSession(currentSession: Session) {
        val editor = preferences.edit()
        editor.putString(context.resources.getString(R.string.shared_preferences_session_code), currentSession.accessCode)
        editor.putString(context.resources.getString(R.string.shared_preferences_session_current_user), currentSession.currentUser)
        editor.putString(context.resources.getString(R.string.shared_preferences_session_previous_user), currentSession.previousUserName)
        editor.apply()
    }

    override fun removeSavedSession() {
        val editor = preferences.edit()
        editor.remove(context.resources.getString(R.string.shared_preferences_session_code))
        editor.remove(context.resources.getString(R.string.shared_preferences_session_current_user))
        editor.remove(context.resources.getString(R.string.shared_preferences_session_previous_user))
        editor.apply()
    }

    override fun getSavedSession(): Session? {
        val accessCode = preferences.getString(
            context.resources.getString(R.string.shared_preferences_session_code),
            null
        ) ?: return null

        val currentUser =  preferences.getString(
            context.resources.getString(R.string.shared_preferences_session_current_user),
            null
        ) ?: return null

        val prevUser =  preferences.getString(
            context.resources.getString(R.string.shared_preferences_session_previous_user),
            currentUser
        ) ?: currentUser

        return Session(accessCode = accessCode, currentUser = currentUser, game = Game.getEmptyGame(), previousUserName = prevUser)
    }
}