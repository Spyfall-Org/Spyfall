package com.dangerfield.werewolf

import com.dangerfield.spyfall.splash.SplashNavigator
import spyfallx.core.doNothing
import spyfallx.coregameapi.Session
import javax.inject.Inject

class InternalSplashNavigator @Inject constructor() : SplashNavigator {
    override fun navigateToWelcome(session: Session?) {
        doNothing()
    }
}
