package com.dangerfield.spyfall.navigation

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.settings.SettingsFragment
import com.dangerfield.spyfall.splash.SplashFragment
import com.dangerfield.spyfall.welcome.WelcomeNavigator
import com.dangerfield.spyfall.welcome.join_game.JoinGameFragment
import com.dangerfield.spyfall.welcome.new_game.NewGameFragment
import com.dangerfield.spyfall.welcome.welcome.WelcomeFragment
import spyfallx.coregameapi.Session
import javax.inject.Inject

/**
 * Class used for navigation from inside of the welcome module
 */
class InternalWelcomeNavigator @Inject constructor(
    private val activity: FragmentActivity
) : WelcomeNavigator {

    override fun navigateToSplash() {
        activity.supportFragmentManager.commit {
            add(R.id.content, SplashFragment())
        }
    }

    override fun navigateToSettings() {
        activity.supportFragmentManager.commit {
            add(R.id.content, SettingsFragment(), SETTINGS)
            addToBackStack(SETTINGS)
        }
    }

    override fun navigateToNewGame() {
        activity.supportFragmentManager.commit {
            add(R.id.content, NewGameFragment(), NEW_GAME)
            addToBackStack(NEW_GAME)
        }
    }

    override fun navigateToJoinGame() {
        activity.supportFragmentManager.commit {
            add(R.id.content, JoinGameFragment(), JOIN_GAME)
            addToBackStack(JOIN_GAME)
        }
    }

    override fun navigateToWelcome(session: Session?) {
        activity.supportFragmentManager.commit {
            add(R.id.content, WelcomeFragment(), WELCOME)
            addToBackStack(WELCOME)
        }
    }

    override fun navigateToRules() {
        Toast.makeText(activity, "Rules", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val SETTINGS = "Settings"
        const val NEW_GAME = "New Game"
        const val JOIN_GAME = "Join Game"
        const val WELCOME = "Welcome"
    }
}
