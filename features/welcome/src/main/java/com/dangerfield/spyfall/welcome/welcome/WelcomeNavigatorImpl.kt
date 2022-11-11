package com.dangerfield.spyfall.welcome.welcome

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import com.dangerfield.spyfall.welcome.R
import com.dangerfield.spyfall.welcome.join_game.JoinGameFragment
import com.dangerfield.spyfall.welcome.new_game.NewGameFragment
import spyfallx.core.Session

class WelcomeNavigatorImpl(
    private val settingsFragmentFactory: SettingsFragmentFactory,
    private val fragmentManager: FragmentManager?
) : WelcomeNavigator {

    override fun navigateToSettings() {
        fragmentManager?.commit {
            add(
                R.id.content,
                settingsFragmentFactory.newInstance(),
            )
            addToBackStack(null)
        }
    }

    override fun navigateToNewGame() {
        fragmentManager?.commit {
            add(
                R.id.content,
                NewGameFragment(),
            )
            addToBackStack(null)
        }
    }

    override fun navigateToJoinGame() {
        fragmentManager?.commit {
            add(
                R.id.content,
                JoinGameFragment(),
            )
            addToBackStack(null)
        }
    }

    override fun navigateToRules() {
    }

    override fun navigateToWelcome(session: Session?) {
        fragmentManager?.commit {
            add(
                R.id.content,
                WelcomeFragment(),
            )
            addToBackStack(null)
        }
    }
}
