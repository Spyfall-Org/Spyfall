package com.dangerfield.spyfall.welcome

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory

class WelcomeNavigator(
    private val settingsFragmentFactory: SettingsFragmentFactory,
    private val activity: AppCompatActivity
    ) {

    private fun navigateToSettings() {
        activity.supportFragmentManager.commit {
            add(
                R.id.content,
                settingsFragmentFactory.newInstance(),
            )
            addToBackStack(null)
        }
    }

    private fun navigateToNewGame() {
        activity.supportFragmentManager.commit {
            add(
                R.id.content,
               NewGameFragment(),
            )
            addToBackStack(null)
        }
    }

    private fun navigateToJoinGame() {
        activity.supportFragmentManager.commit {
            add(
                R.id.content,
                JoinGameFragment(),
            )
            addToBackStack(null)
        }
    }
}