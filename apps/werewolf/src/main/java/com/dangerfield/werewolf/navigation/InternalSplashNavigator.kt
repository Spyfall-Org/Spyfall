package com.dangerfield.werewolf.navigation

import com.dangerfield.spyfall.splash.splash.SplashNavigator
import spyfallx.core.doNothing
import spyfallx.coregameapi.Session
import javax.inject.Inject

class InternalSplashNavigator @Inject constructor() : SplashNavigator {
    override fun navigateToWelcome(session: Session?) {
        doNothing()
    }

    override fun navigateToForcedUpdate() {
        doNothing()
    }
}
