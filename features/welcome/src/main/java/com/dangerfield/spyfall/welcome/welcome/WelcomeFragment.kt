package com.dangerfield.spyfall.welcome.welcome


import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.welcome.R
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import spyfallx.core.Session
import spyfallx.coreui.viewScoped

private const val KEY_SESSION = "KEY_SESSION"

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val session: Session?
        get() = arguments?.getParcelable(KEY_SESSION)

    companion object : WelcomeFragmentFactory {
        override fun newInstance(session: Session?) = WelcomeFragment().apply {
            arguments = bundleOf(KEY_SESSION to session )
        }
    }
}