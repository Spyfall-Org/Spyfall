package com.dangerfield.spyfall.navigation

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.legacy.ui.settings.RequireUpdateFragment
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
        navigateToAsRoot(WELCOME) {
            WelcomeFragment()
        }
    }

    override fun navigateToRules() {
        Toast.makeText(activity, "Rules", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAsRoot(tag: String, factory: () -> Fragment) {
        if (activity.supportFragmentManager.backStackEntryCount > 0) {
            val backStackRootId = activity.supportFragmentManager.getBackStackEntryAt(0).id
            activity.supportFragmentManager.popBackStack(backStackRootId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        activity.supportFragmentManager.commit {
            replace(R.id.content, factory.invoke(), tag)
        }
    }

    companion object {
        const val SETTINGS = "Settings"
        const val NEW_GAME = "New Game"
        const val JOIN_GAME = "Join Game"
        const val WELCOME = "Welcome"
    }
}
