package com.dangerfield.spyfall

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.commit
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.MainActivityViewModel.Step.ForceUpdateDecision
import com.dangerfield.spyfall.MainActivityViewModel.Step.SplashDecision
import com.dangerfield.spyfall.legacy.util.ThemeChangeableActivity
import com.dangerfield.spyfall.legacy.util.isLegacyBuild
import com.dangerfield.spyfall.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import spyfallx.core.BuildInfo
import spyfallx.coreui.collectWhileStarted
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ThemeChangeableActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModel()

    @Inject
    lateinit var buildInfo: BuildInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (buildInfo.isLegacySpyfall) {
            setContentView(R.layout.activity_main_legacy)
        } else {
            setContentView(R.layout.activity_main)
        }

        mainActivityViewModel.state.collectWhileStarted(this) { state ->
            when (state.step) {
                is ForceUpdateDecision -> if (state.step.shouldShowForceUpdate) {
                    Log.d("Elijah", "forcing update")
                    showBlockingUpdateLegacy()
                }
                is SplashDecision -> if (state.step.shouldShowSplash) {
                    Log.d("Elijah", "showing splash")
                    supportFragmentManager.commit { add(R.id.content, SplashFragment()) }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (isLegacyBuild()) {
            findNavController(this, R.id.nav_host_fragment).navigateUp()
        } else {
            super.onSupportNavigateUp()
        }
    }

    private fun showBlockingUpdateLegacy() {
        val navController = findNavController(this, R.id.nav_host_fragment)
        if (navController.currentDestination?.id != R.id.requireUpdateFragment) {
            navController.navigate(R.id.action_startFragment_to_requireUpdateFragment)
        }
    }
}
