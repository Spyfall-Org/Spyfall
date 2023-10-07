package com.dangerfield.spyfall.legacy.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dangerfield.spyfall.R
import dagger.hilt.android.AndroidEntryPoint
import com.dangerfield.spyfall.legacy.util.collectWhileStarted
import com.dangerfield.spyfall.legacy.util.viewScoped
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val splashViewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var presenterFactory: Provider<SplashPresenter>

    private val presenter by viewScoped { presenterFactory.get() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splashViewModel.state.collectWhileStarted(viewLifecycleOwner) {
            presenter?.bindState(it)
        }
    }
}
