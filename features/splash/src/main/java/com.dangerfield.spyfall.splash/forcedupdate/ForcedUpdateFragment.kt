package com.dangerfield.spyfall.splash.forcedupdate

import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.splash.R
import dagger.hilt.android.AndroidEntryPoint
import spyfallx.coreui.viewScope
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ForcedUpdateFragment : Fragment(R.layout.fragment_forced_update) {

    @Inject
    lateinit var presenterProvider: Provider<ForcedUpdatePresenter>

    init {
        viewScope { presenterProvider.get() }
    }

}
