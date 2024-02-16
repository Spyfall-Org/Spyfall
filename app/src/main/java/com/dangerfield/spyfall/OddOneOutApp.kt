package com.dangerfield.spyfall

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.LocalAdsConfig
import com.dangerfield.features.blockingerror.blockingErrorRoute
import com.dangerfield.features.blockingerror.maintenanceRoute
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.analytics.LocalMetricsTracker
import com.dangerfield.libraries.analytics.MetricsTracker
import com.dangerfield.libraries.coreflowroutines.observeWithLifecycle
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.LanguageSupportLevel
import com.dangerfield.libraries.dictionary.LocalDictionary
import com.dangerfield.libraries.dictionary.internal.ui.navigateToLanguageSupportDialog
import com.dangerfield.libraries.navigation.BlockingScreenRouter
import com.dangerfield.libraries.navigation.fadeInToEndAnim
import com.dangerfield.libraries.navigation.fadeInToStartAnim
import com.dangerfield.libraries.navigation.fadeOutToEndAnim
import com.dangerfield.libraries.navigation.fadeOutToStartAnim
import com.dangerfield.libraries.navigation.floatingwindow.FloatingWindowHost
import com.dangerfield.libraries.navigation.floatingwindow.FloatingWindowNavigator
import com.dangerfield.libraries.navigation.floatingwindow.getFloatingWindowNavigator
import com.dangerfield.libraries.navigation.internal.NavControllerRouter
import com.dangerfield.libraries.network.NetworkMonitor
import com.dangerfield.libraries.ui.LocalAppState
import com.dangerfield.libraries.ui.LocalBuildInfo
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Snackbar
import com.dangerfield.libraries.ui.components.isDebugMessage
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.toSnackbarData
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.spyfall.navigation.NavBuilderRegistry
import com.dangerfield.spyfall.startup.MainActivityViewModel
import kotlinx.coroutines.flow.receiveAsFlow
import oddoneout.core.BuildInfo
import oddoneout.core.UserMessagePresenter
import spyfallx.ui.color.background

@Suppress("MagicNumber")
@Composable
fun OddOneOutApp(
    accentColor: ColorPrimitive,
    navBuilderRegistry: NavBuilderRegistry,
    networkMonitor: NetworkMonitor,
    adsConfig: AdsConfig,
    blockingScreenRouter: BlockingScreenRouter,
    isInMaintenanceMode: Boolean,
    metricsTracker: MetricsTracker,
    onLanguageSupportLevelMessageShown: (LanguageSupportLevel) -> Unit,
    languageSupportLevelMessage: MainActivityViewModel.LanguageSupportLevelMessage?,
    dictionary: Dictionary,
    buildInfo: BuildInfo,
    appState: OddOneOutAppState = rememberAppState(networkMonitor = networkMonitor),
    isUpdateRequired: Boolean,
    hasBlockingError: Boolean,
) {
    val navController = rememberNavController(FloatingWindowNavigator())
    val coroutineScope = rememberCoroutineScope()
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val blockingScreenToShow by blockingScreenRouter
        .routes
        .receiveAsFlow()
        .collectAsStateWithLifecycle(initialValue = null)

    val router = remember {
        NavControllerRouter(
            navHostController = navController,
            coroutineScope = coroutineScope
        )
    }


    val startingRoute = when {
        isUpdateRequired -> forcedUpdateNavigationRoute.noArgRoute()
        isInMaintenanceMode -> maintenanceRoute.noArgRoute()
        hasBlockingError -> blockingErrorRoute.noArgRoute()
        blockingScreenToShow != null -> blockingScreenToShow ?: welcomeNavigationRoute.noArgRoute()
        else -> welcomeNavigationRoute.noArgRoute()
    }

    LaunchedEffect(Unit) {
        UserMessagePresenter
            .messages
            .receiveAsFlow()
            .observeWithLifecycle(lifecycleOwner.lifecycle) {
                snackbarHostState.showSnackbar(
                    message = it.message,
                    withDismissAction = !it.autoDismiss,
                    duration = if (it.autoDismiss) SnackbarDuration.Short else SnackbarDuration.Indefinite
                )
            }
    }

    // TODO add maintainence mode
    CompositionLocalProvider(
        LocalAdsConfig provides adsConfig,
        LocalMetricsTracker provides metricsTracker,
        LocalDictionary provides dictionary,
        LocalBuildInfo provides buildInfo,
        LocalAppState provides appState
    ) {
        OddOneOutTheme(
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
                Column(
                    modifier = Modifier.padding(it)
                ) {
                    AnimatedVisibility(
                        visible = isOffline,
                        enter = slideInVertically(),
                        exit = slideOutVertically()
                    ) {
                        // TODO cleanup make this a component
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(OddOneOutTheme.colorScheme.textWarning),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Odd One Out is offline",
                                typographyToken = OddOneOutTheme.typography.Body.B700,
                                color = OddOneOutTheme.colorScheme.text,
                            )
                        }
                    }

                    // TODO fix the padding with bottom sheets and text fields
                    NavHost(
                        navController = router.navHostController,
                        startDestination = startingRoute.route,
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

                    LaunchedEffect(languageSupportLevelMessage) {
                        if (languageSupportLevelMessage != null) {

                            onLanguageSupportLevelMessageShown(languageSupportLevelMessage.languageSupportLevel)

                            router.navigateToLanguageSupportDialog(
                                supportLevelName = languageSupportLevelMessage.languageSupportLevel.name,
                                languageDisplayName = languageSupportLevelMessage.languageSupportLevel.locale.displayLanguage
                            )
                        }
                    }
                }

                val bottomSheetNavigator = router.navHostController.getFloatingWindowNavigator()

                bottomSheetNavigator?.let {
                    FloatingWindowHost(bottomSheetNavigator)
                }
            }
        }
    }
}
