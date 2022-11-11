package com.dangerfield.spyfall.welcome.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dangerfield.spyfall.welcome.R
import dagger.hilt.android.AndroidEntryPoint
import spyfallx.coreui.collectWhileStarted
import spyfallx.coreui.viewScoped
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    @Inject
    private lateinit var splashViewModel: SplashViewModel

    private val presenterFactory by inject<SplashPresenterFactory>()

    private val presenter by viewScoped { presenterFactory.invoke(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splashViewModel.state.collectWhileStarted(viewLifecycleOwner) {
            presenter?.bindState(it)
        }
    }
}
