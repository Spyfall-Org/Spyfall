package com.dangerfield.spyfall.welcomeapi

import androidx.fragment.app.Fragment

interface WelcomeFragmentFactory {
    fun newInstance(): Fragment
}