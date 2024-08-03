package com.dangerfield.features.newgame.internal.presentation

import android.content.SharedPreferences
import javax.inject.Inject

class NewGamePrefs @Inject constructor(
    private val sharedPreferences: SharedPreferences
){

    var hasUsedCreateYourOwn: Boolean
        get() = sharedPreferences.getBoolean(HAS_USED_CREATE_YOUR_OWN, true)
        set(value) = sharedPreferences.edit().putBoolean(HAS_USED_CREATE_YOUR_OWN, value).apply()

    companion object {
        private const val HAS_USED_CREATE_YOUR_OWN = "HAS_USED_CREATE_YOUR_OWN"
    }
}