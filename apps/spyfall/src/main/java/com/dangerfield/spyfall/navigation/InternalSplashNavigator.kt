package com.dangerfield.spyfall.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
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
        navigateToAsRoot(InternalWelcomeNavigator.WELCOME) {
            WelcomeFragment()
        }
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
        const val WELCOME = "Welcome"
    }
}
