package com.dangerfield.spyfall.legacy.ui.restart

import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.legacy.ui.forcedupdate.ForcedUpdatePresenter
import dagger.hilt.android.AndroidEntryPoint
import com.dangerfield.spyfall.legacy.util.viewScope
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class RestartFragment : Fragment(R.layout.fragment_restart) {

    @Inject
    lateinit var presenterProvider: Provider<RestartPresenter>

    init {
        viewScope { presenterProvider.get() }
    }

}
