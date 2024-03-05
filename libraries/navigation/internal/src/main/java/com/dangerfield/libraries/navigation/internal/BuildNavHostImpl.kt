package com.dangerfield.libraries.navigation.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dangerfield.libraries.navigation.BuildNavHost
import com.dangerfield.libraries.navigation.fadeInToEndAnim
import com.dangerfield.libraries.navigation.fadeInToStartAnim
import com.dangerfield.libraries.navigation.fadeOutToEndAnim
import com.dangerfield.libraries.navigation.fadeOutToStartAnim
import com.dangerfield.libraries.navigation.floatingwindow.FloatingWindowHost
import com.dangerfield.libraries.navigation.floatingwindow.FloatingWindowNavigator
import com.dangerfield.libraries.navigation.floatingwindow.getFloatingWindowNavigator
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject

@AutoBind
@Stable
class BuildNavHostImpl @Inject constructor(
    private val navBuilderRegistry: NavBuilderRegistry,
    private val delegatingRouter: DelegatingRouter
): BuildNavHost {
    @Composable
    override fun invoke(startingRoute: String) {

        val navController = rememberNavController(FloatingWindowNavigator())
        val coroutineScope = rememberCoroutineScope()
        val lifecycle = LocalLifecycleOwner.current.lifecycle

        val actualRouter = remember {
            NavControllerRouter(
                navHostController = navController,
                coroutineScope = coroutineScope
            )
        }

        Timber.d("Setting navigation up with starting route: $startingRoute")

        NavHost(
            navController = actualRouter.navHostController,
            startDestination = startingRoute,
            enterTransition = { fadeInToStartAnim() },
            exitTransition = { fadeOutToStartAnim() },
            popEnterTransition = { fadeInToEndAnim() },
            popExitTransition = { fadeOutToEndAnim() }
        ) {
            navBuilderRegistry.registerNavBuilderForModule(navGraphBuilder = this)
            delegatingRouter.setDelegate(actualRouter, lifecycle)
        }

        val bottomSheetNavigator = remember {
            actualRouter.navHostController.getFloatingWindowNavigator()
        }

        bottomSheetNavigator?.let {
            FloatingWindowHost(bottomSheetNavigator)
        }
    }
}