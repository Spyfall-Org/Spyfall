package com.dangerfield.spyfall

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dangerfield.features.blockingerror.blockingErrorRoute
import com.dangerfield.features.blockingerror.maintenanceRoute
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.ConsentStatus.ConsentDenied
import com.dangerfield.features.consent.ConsentStatus.ConsentNeeded
import com.dangerfield.features.consent.consentRoute
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.features.inAppMessaging.UpdateStatus
import com.dangerfield.features.inAppMessaging.internal.update.DownloadProgressBar
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.coreflowroutines.observeWithLifecycle
import com.dangerfield.libraries.network.internal.OfflineBar
import com.dangerfield.libraries.ui.LocalAppState
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.components.OddOneOutSnackbarVisuals
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Snackbar
import com.dangerfield.libraries.ui.components.oddOneOutSnackbarData
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import kotlinx.coroutines.flow.receiveAsFlow
import oddoneout.core.SnackBarPresenter
import timber.log.Timber

@Composable
fun OddOneOutApp(
    accentColor: ColorResource,
    isInMaintenanceMode: Boolean,
    consentStatus: ConsentStatus?,
    updateStatus: UpdateStatus?,
    isUpdateRequired: Boolean,
    hasBlockingError: Boolean,
    buildNavHost: @Composable (String) -> Unit
) {

    val isOffline by LocalAppState.current.isOffline.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    val startingRoute by remember(
        isUpdateRequired,
        isInMaintenanceMode,
        hasBlockingError,
        consentStatus
    ) {
        derivedStateOf {
            when {
                isUpdateRequired -> forcedUpdateNavigationRoute.noArgRoute()
                isInMaintenanceMode -> maintenanceRoute.noArgRoute()
                hasBlockingError -> blockingErrorRoute.noArgRoute()
                consentStatus in listOf(ConsentDenied, ConsentNeeded) -> consentRoute.noArgRoute()
                else -> welcomeNavigationRoute.noArgRoute()
            }.also {
                Timber.d("Starting route changed: ${it.route}")
            }
        }
    }

    LaunchedEffect(Unit) {
        SnackBarPresenter
            .messages
            .receiveAsFlow()
            .observeWithLifecycle(lifecycleOwner.lifecycle) {
                val result = snackbarHostState.showSnackbar(
                    OddOneOutSnackbarVisuals(
                        message = it.message,
                        isDebug = it.isDebug,
                        withDismissAction = !it.autoDismiss,
                        duration = if (it.autoDismiss) SnackbarDuration.Short else SnackbarDuration.Indefinite,
                        title = it.title,
                        actionLabel = it.actionLabel
                    )
                )

                if (result == SnackbarResult.ActionPerformed) {
                    it.action?.invoke()
                }
            }
    }
    OddOneOutTheme(
        themeColor = accentColor
    ) {
        Screen(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { snackbarData ->
                        (snackbarData.visuals as? OddOneOutSnackbarVisuals)?.let {
                            val data = remember {
                                oddOneOutSnackbarData(
                                    visuals = it,
                                    onAction = snackbarData::performAction,
                                    onDismiss = snackbarData::dismiss
                                )
                            }

                            Snackbar(
                                oddOneOutSnackbarData = data
                            )
                        }
                    }
                )
            },
        ) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                OfflineBar(isOffline)
                DownloadProgressBar(updateStatus)
                buildNavHost(startingRoute.route)
            }
        }
    }
}
