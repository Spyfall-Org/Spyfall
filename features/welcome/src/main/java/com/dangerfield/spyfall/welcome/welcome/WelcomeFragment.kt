package com.dangerfield.spyfall.welcome.welcome


import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.welcome.R
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.android.ext.android.inject
import spyfallx.coreui.viewScoped

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val presenterFactory: WelcomePresenterFactory by inject()

    private val presenter by viewScoped { presenterFactory.create(this) }

    companion object : WelcomeFragmentFactory {
        override fun newInstance() = WelcomeFragment()
    }
}