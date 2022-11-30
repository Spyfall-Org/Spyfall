package com.dangerfield.spyfall.splash

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import spyfallx.coreui.viewScoped
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ForcedUpdateFragment : Fragment(R.layout.fragment_forced_update) {

    @Inject
    lateinit var presenterProvider: Provider<ForcedUpdatePresenter>

    private val presenter by viewScoped { presenterProvider.get() }

}
