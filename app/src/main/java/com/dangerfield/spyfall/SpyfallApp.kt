package com.dangerfield.spyfall

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dangerfield.features.blockingerror.blockingErrorRoute
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.coreflowroutines.observeWithLifecycle
import com.dangerfield.libraries.navigation.floatingwindow.FloatingWindowHost
import com.dangerfield.libraries.navigation.floatingwindow.FloatingWindowNavigator
import com.dangerfield.libraries.navigation.floatingwindow.getFloatingWindowNavigator
import com.dangerfield.libraries.navigation.fadeInToEndAnim
import com.dangerfield.libraries.navigation.fadeInToStartAnim
import com.dangerfield.libraries.navigation.fadeOutToEndAnim
import com.dangerfield.libraries.navigation.fadeOutToStartAnim
import com.dangerfield.libraries.navigation.internal.NavControllerRouter
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Snackbar
import com.dangerfield.libraries.ui.components.isDebugMessage
import com.dangerfield.libraries.ui.components.toSnackbarData
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import kotlinx.coroutines.flow.receiveAsFlow
import spyfallx.core.UserMessagePresenter

@Suppress("MagicNumber")
@Composable
fun SpyfallApp(
    accentColor: ColorPrimitive,
    navBuilderRegistry: NavBuilderRegistry,
    isUpdateRequired: Boolean,
    hasBlockingError: Boolean,
) {
    val navController = rememberNavController(FloatingWindowNavigator())
    val coroutineScope = rememberCoroutineScope()

    val router = remember {
        NavControllerRouter(
            navHostController = navController,
            coroutineScope = coroutineScope
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        UserMessagePresenter
            .messages
            .receiveAsFlow()
            .observeWithLifecycle(lifecycleOwner.lifecycle) {
                snackbarHostState.showSnackbar(
                    message = it.message,
                    withDismissAction = !it.autoDismiss,
                    duration = if(it.autoDismiss) SnackbarDuration.Short else SnackbarDuration.Indefinite
                )
            }
    }

    val startingRoute = when {
        isUpdateRequired -> forcedUpdateNavigationRoute
        hasBlockingError -> blockingErrorRoute
        else -> welcomeNavigationRoute
    }
    // TODO add maintainence mode
    // maybe have main activity view model observe the config and update the app state.
    // having an app state here would be great

    OddOneOutTheme(
        isDarkMode = isSystemInDarkTheme(),
        themeColor = accentColor
    ) {
        Screen(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = {
                        Snackbar(
                            isDebugMessage = it.isDebugMessage(),
                            snackbarData = it.toSnackbarData()
                        )
                    }
                )
            },
        ) {
            NavHost(
                modifier = Modifier.imePadding(),
                navController = router.navHostController,
                startDestination = startingRoute.navRoute,
                enterTransition = { fadeInToStartAnim() },
                exitTransition = { fadeOutToStartAnim() },
                popEnterTransition = { fadeInToEndAnim() },
                popExitTransition = { fadeOutToEndAnim() }
            ) {
                navBuilderRegistry.registerNavBuilderForModule(
                    navGraphBuilder = this,
                    router = router
                )
            }

            val bottomSheetNavigator = router.navHostController.getFloatingWindowNavigator()

            bottomSheetNavigator?.let {
                FloatingWindowHost(bottomSheetNavigator)
            }
        }
    }
}
