package com.dangerfield.spyfall

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.MainActivityViewModel.State
import com.dangerfield.spyfall.legacy.util.collectWhileStarted
import com.dangerfield.spyfall.legacy.util.isLegacyBuild
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.viewModel
import spyfallx.core.BuildInfo
import spyfallx.core.doNothing
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.setContent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModel()

    @Inject
    lateinit var buildInfo: BuildInfo

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

        applicationContext.setContent(
            accentColor = ColorPrimitive.CherryPop700,
        ) {
            val appState by mainActivityViewModel.state.collectAsState()

            when(val smartState = appState) {
                is State.Loaded -> {
                    SpyfallApp(
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
