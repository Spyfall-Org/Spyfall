package com.dangerfield.spyfall.settingsapi

import androidx.fragment.app.Fragment

interface SettingsFragmentFactory {
    fun newInstance(): Fragment
}
