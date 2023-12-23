package com.dangerfield.spyfall

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dangerfield.libraries.network.NetworkMonitor
import com.dangerfield.libraries.session.DarkModeConfig
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.spyfall.legacy.util.collectWhileStarted
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import com.dangerfield.spyfall.startup.MainActivityViewModel
import com.dangerfield.spyfall.startup.MainActivityViewModel.State
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Error
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Loaded
import com.dangerfield.spyfall.startup.MainActivityViewModel.State.Loading
import com.dangerfield.spyfall.startup.SplashScreenBuilder
import dagger.hilt.android.AndroidEntryPoint
import spyfallx.core.doNothing
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private var hasSetContent = AtomicBoolean(false)

    @Inject
    lateinit var navBuilderRegistry: NavBuilderRegistry

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        var isLoading: Boolean by mutableStateOf(false)

        SplashScreenBuilder(this)
            .keepOnScreenWhile { isLoading }
            .build()

        super.onCreate(savedInstanceState) // should be called after splash screen builder

        // Delay set content so we can animate splash screen views
        collectWhileStarted(mainActivityViewModel.state) { state ->
            isLoading = state is Loading
            when  {
                state is Loading -> doNothing()
                state !is Loading && !hasSetContent.getAndSet(true) -> setAppContent()
            }
        }
    }

    private fun setAppContent() {
        setContent {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            val state by mainActivityViewModel.state.collectAsStateWithLifecycle()

            // TODO make the app still usable when there is an error.
            // we dont need a session and such to let the user use the app.
            SpyfallApp(
                navBuilderRegistry = navBuilderRegistry,
                isUpdateRequired = state.isUpdateRequired,
                hasBlockingError = state is Error,
                accentColor = state.accentColor,
                darkModeConfig = state.darkModeConfig,
                networkMonitor = networkMonitor
            )
        }
    }

    private val State.accentColor: ColorPrimitive
        get() = (this as? Loaded)?.accentColor
            ?: ThemeColor.CherryPop700.colorPrimitive

    private val State.isUpdateRequired: Boolean
        get() = this is Loaded && isUpdateRequired

    private val State.darkModeConfig: DarkModeConfig
        get() = (this as? Loaded)?.darkModeConfig
            ?: DarkModeConfig.System
}
