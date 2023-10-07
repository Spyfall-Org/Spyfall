package com.dangerfield.spyfall.legacy.ui.splash

import spyfallx.coregameapi.Session

interface SplashNavigator {
    fun navigateToWelcome(session: Session?)
    fun navigateToForcedUpdate()
}
