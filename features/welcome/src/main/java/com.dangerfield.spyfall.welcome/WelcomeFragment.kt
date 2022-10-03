package com.dangerfield.spyfall.welcome

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.android.ext.android.inject

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val settingsFragmentFactory : SettingsFragmentFactory by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_settings).let {
            it.setOnClickListener {
                navigateToSettings()
            }
        }
    }

    private fun navigateToSettings() {
        requireActivity().supportFragmentManager.commit {
            add(
                R.id.content,
                settingsFragmentFactory.newInstance(),
            )
            addToBackStack(null)
        }
    }

    companion object : WelcomeFragmentFactory {
        override fun newInstance() = WelcomeFragment()
    }
}