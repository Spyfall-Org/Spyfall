package com.dangerfield.spyfall.splash.splash

import spyfallx.coregameapi.Session

interface SplashNavigator {
    fun navigateToWelcome(session: Session?)
    fun navigateToForcedUpdate()
}
