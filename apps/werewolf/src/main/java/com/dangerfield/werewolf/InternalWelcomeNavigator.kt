package com.dangerfield.werewolf

import com.dangerfield.spyfall.welcome.WelcomeNavigator
import spyfallx.core.doNothing
import spyfallx.coregameapi.Session
import javax.inject.Inject

class InternalWelcomeNavigator @Inject constructor() : WelcomeNavigator {
    override fun navigateToSettings() {
        doNothing()
    }

    override fun navigateToNewGame() {
        doNothing()
    }

    override fun navigateToJoinGame() {
        doNothing()
    }

    override fun navigateToWelcome(session: Session?) {
        doNothing()
    }

    override fun navigateToRules() {
        doNothing()
    }

    override fun navigateToSplash() {
        doNothing()
    }
}
