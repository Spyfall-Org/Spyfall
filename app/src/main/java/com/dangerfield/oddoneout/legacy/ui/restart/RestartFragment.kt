package com.dangerfield.oddoneout.legacy.ui.restart

import androidx.fragment.app.Fragment
import com.dangerfield.oddoneout.R
import dagger.hilt.android.AndroidEntryPoint
import com.dangerfield.oddoneout.legacy.util.viewScope
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
