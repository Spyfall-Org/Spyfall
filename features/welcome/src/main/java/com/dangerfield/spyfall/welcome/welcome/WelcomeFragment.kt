package com.dangerfield.spyfall.welcome.welcome

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.welcome.R
import dagger.hilt.android.AndroidEntryPoint
import spyfallx.coregameapi.Session
import spyfallx.coreui.viewScoped
import javax.inject.Inject
import javax.inject.Provider

private const val KEY_SESSION = "KEY_SESSION"

@AndroidEntryPoint
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    @Inject
    lateinit var presenterFactory: Provider<WelcomePresenter>

    private val presenter by viewScoped { presenterFactory.get() }

    private val session: Session?
        get() = arguments?.getParcelable(KEY_SESSION)

    companion object {
        fun newInstance(session: Session?) = WelcomeFragment().apply {
            arguments = bundleOf(KEY_SESSION to session)
        }
    }
}
