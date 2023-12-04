package com.dangerfield.spyfall

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.legacy.util.ThemeChangeableActivity
import com.dangerfield.spyfall.legacy.util.collectWhileStarted
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import com.dangerfield.spyfall.startup.IsComposeRefactor
import com.dangerfield.spyfall.startup.MainActivityViewModel
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Error
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Loaded
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Loading
import com.dangerfield.spyfall.startup.SplashScreenBuilder
import dagger.hilt.android.AndroidEntryPoint
import spyfallx.core.doNothing
import spyfallx.ui.color.AccentColor
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ThemeChangeableActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var navBuilderRegistry: NavBuilderRegistry

    @Inject
    lateinit var isSpyfallV2: IsComposeRefactor

    private var hasCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        var isLoading: Boolean by mutableStateOf(value = true)

        SplashScreenBuilder(this)
            .keepOnScreenWhile { isLoading }
            .build()

        super.onCreate(savedInstanceState) // should be called after splash screen builder

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        collectWhileStarted(mainActivityViewModel.state) {
            isLoading = it is Loading
            when (it) {
                is Loading -> doNothing()
                is Error -> create(isUpdateRequired = false, hadErrorLoadingApp = true)
                is Loaded -> create(
                    isUpdateRequired = it.isUpdateRequired,
                    hadErrorLoadingApp = false
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = if (isSpyfallV2()) {
        super.onSupportNavigateUp()
    } else {
        findNavController(this, R.id.nav_host_fragment).navigateUp()
    }

    private fun create(
        isUpdateRequired: Boolean,
        hadErrorLoadingApp: Boolean
    ) {
        if (hasCreated) return
        hasCreated = true

        if (isSpyfallV2()) {
            refactorOnCreate(isUpdateRequired, hadErrorLoadingApp)
        } else {
            legacyOnCreate(isUpdateRequired, hadErrorLoadingApp)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun legacyOnCreate(isUpdateRequired: Boolean, hadErrorLoadingApp: Boolean) {
        setContentView(R.layout.activity_main_legacy)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (isUpdateRequired) showBlockingUpdateLegacy()
        if (hadErrorLoadingApp) showBlockingRestart()
    }

    private fun refactorOnCreate(isUpdateRequired: Boolean, hadErrorLoadingApp: Boolean) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        setContent {
            SpyfallApp(
                navBuilderRegistry = navBuilderRegistry,
                isUpdateRequired = isUpdateRequired,
                hasBlockingError = hadErrorLoadingApp,
                accentColor = AccentColor.entries.random()
            )
        }
    }

    private fun showBlockingUpdateLegacy() {
        val navController = findNavController(this, R.id.nav_host_fragment)
        if (navController.currentDestination?.id != R.id.requireUpdateFragment) {
            navController.navigate(R.id.action_startFragment_to_requireUpdateFragment)
        }
    }

    private fun showBlockingRestart() {
        val navController = findNavController(this, R.id.nav_host_fragment)
        if (navController.currentDestination?.id != R.id.restartFragment) {
            navController.navigate(R.id.action_startFragment_to_restartFragment)
        }
    }
}
