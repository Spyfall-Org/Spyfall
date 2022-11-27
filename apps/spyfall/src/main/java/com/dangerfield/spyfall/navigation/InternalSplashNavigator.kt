package com.dangerfield.spyfall.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.splash.SplashNavigator
import com.dangerfield.spyfall.welcome.welcome.WelcomeFragment
import spyfallx.coregameapi.Session
import javax.inject.Inject

/**
 * Class used for navigation from inside of the splash module
 */
class InternalSplashNavigator @Inject constructor(
    private val activity: FragmentActivity
) : SplashNavigator {

    override fun navigateToWelcome(session: Session?) {
        activity.supportFragmentManager.commit {
            add(R.id.content, WelcomeFragment.newInstance(session), WELCOME)
            addToBackStack(WELCOME)
        }
    }

    companion object {
        const val WELCOME = "Welcome"
    }
}
