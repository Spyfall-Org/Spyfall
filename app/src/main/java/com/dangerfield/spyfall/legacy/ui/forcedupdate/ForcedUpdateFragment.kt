package com.dangerfield.spyfall.legacy.ui.forcedupdate

import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.R
import dagger.hilt.android.AndroidEntryPoint
import com.dangerfield.spyfall.legacy.util.viewScope
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
