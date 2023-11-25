package com.dangerfield.spyfall

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.dangerfield.spyfall.legacy.util.ThemeChangeableActivity
import com.dangerfield.spyfall.legacy.util.collectWhileStarted
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import com.dangerfield.spyfall.startup.IsLegacyBuild
import com.dangerfield.spyfall.startup.MainActivityViewModel
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Error
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Loaded
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Loading
import com.dangerfield.spyfall.startup.SplashScreenBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import spyfallx.core.doNothing
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class MainActivity : ThemeChangeableActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var navBuilderRegistry: NavBuilderRegistry

    @Inject
    lateinit var isLegacyBuildUseCase: IsLegacyBuild

    // WARNING: using this before the state is loaded will cause a blocking get of the app config
    private val isLegacyBuild: Boolean get() = isLegacyBuildUseCase()

    private var hasCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        var isLoading: Boolean by mutableStateOf(value = true)

        SplashScreenBuilder(this)
            .keepOnScreenWhile { isLoading }
            .build()

        super.onCreate(savedInstanceState) // should be called after splash screen builder

        collectWhileStarted(mainActivityViewModel.state) {
            isLoading = it is Loading
            when (it) {
                is Loading -> doNothing()
                is Error -> create(isUpdateRequired = false, hadErrorLoadingApp = true)
                is Loaded -> create(isUpdateRequired = it.isUpdateRequired, hadErrorLoadingApp = false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = if (isLegacyBuild) {
        findNavController(this, R.id.nav_host_fragment).navigateUp()
    } else {
        super.onSupportNavigateUp()
    }

    private fun create(
        isUpdateRequired: Boolean,
        hadErrorLoadingApp: Boolean
    ) {
        if (hasCreated) return
        hasCreated = true

        if (isLegacyBuild) {
            legacyOnCreate(isUpdateRequired, hadErrorLoadingApp)
        } else {
            refactorOnCreate(isUpdateRequired, hadErrorLoadingApp)
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
                hadErrorLoadingApp = hadErrorLoadingApp
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
