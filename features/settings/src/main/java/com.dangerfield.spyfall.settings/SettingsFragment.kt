package com.dangerfield.spyfall.settings

import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    companion object : SettingsFragmentFactory {
        override fun newInstance(): Fragment = SettingsFragment()
    }
}
