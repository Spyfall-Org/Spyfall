package com.dangerfield.oddoneout.legacy.ui.forcedupdate

import androidx.fragment.app.Fragment
import com.dangerfield.oddoneout.R
import dagger.hilt.android.AndroidEntryPoint
import com.dangerfield.oddoneout.legacy.util.viewScope
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
