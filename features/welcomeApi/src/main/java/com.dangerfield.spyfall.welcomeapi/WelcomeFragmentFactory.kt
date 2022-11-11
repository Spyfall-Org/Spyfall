package com.dangerfield.spyfall.welcomeapi

import androidx.fragment.app.Fragment
import spyfallx.core.Session

interface WelcomeFragmentFactory {
    fun newInstance(session: Session?): Fragment
}
