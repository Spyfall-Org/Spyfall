package com.dangerfield.spyfall

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.MainActivityViewModel.State
import com.dangerfield.spyfall.legacy.util.ThemeChangeableActivity
import com.dangerfield.spyfall.legacy.util.collectWhileStarted
import com.dangerfield.spyfall.legacy.util.isLegacyBuild
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import spyfallx.core.BuildInfo
import spyfallx.core.doNothing
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ThemeChangeableActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModel()

    @Inject
    lateinit var buildInfo: BuildInfo

    @Inject
    lateinit var navBuilderRegistry: NavBuilderRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var isLoading: Boolean by mutableStateOf(value = false)

        collectWhileStarted(mainActivityViewModel.state) {
            isLoading = it == State.Loading

            if (buildInfo.isLegacySpyfall && it is State.Loaded && it.isUpdateRequired) {
                showBlockingUpdateLegacy()
            }
        }

        splashScreen.setKeepOnScreenCondition { isLoading }

        if (buildInfo.isLegacySpyfall) {
            legacyOnCreate()
        } else {
            refactorOnCreate()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun legacyOnCreate() {
        setContentView(R.layout.activity_main_legacy)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun refactorOnCreate() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        setContent {
            val uiState by mainActivityViewModel.state.collectAsState()

            when(val smartState = uiState) {
                is State.Loaded -> {
                    SpyfallApp(
                        navBuilderRegistry = navBuilderRegistry,
                        isUpdateRequired = smartState.isUpdateRequired,
                    )
                }
                State.Loading -> doNothing()
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
