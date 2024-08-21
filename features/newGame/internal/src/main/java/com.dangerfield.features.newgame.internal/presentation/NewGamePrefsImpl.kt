package com.dangerfield.features.newgame.internal.presentation

import android.content.SharedPreferences
import com.dangerfield.features.newgame.NewGamePrefs
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class NewGamePrefsImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): NewGamePrefs {

    override var hasUsedCreateYourOwnPack: Boolean
        get() = sharedPreferences.getBoolean(HAS_USED_CREATE_YOUR_OWN, false)
        set(value) = sharedPreferences.edit().putBoolean(HAS_USED_CREATE_YOUR_OWN, value).apply()

    companion object {
        private const val HAS_USED_CREATE_YOUR_OWN = "HAS_USED_CREATE_YOUR_OWN"
    }

}