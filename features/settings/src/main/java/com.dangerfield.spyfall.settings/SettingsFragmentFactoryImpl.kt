package com.dangerfield.spyfall.settings

import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory

class SettingsFragmentFactoryImpl : SettingsFragmentFactory {
    override fun get(): Fragment = SettingsFragment.get()
}