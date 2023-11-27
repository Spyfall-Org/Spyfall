package com.dangerfield.features.settings.internal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.qa.navigateToQa
import com.dangerfield.features.settings.navigateToSettings
import com.dangerfield.features.settings.settingsNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.BuildInfo
import spyfallx.ui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class SettingsModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo
): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = settingsNavigationRoute,
        ) {
            SettingsScreen(
                versionName = buildInfo.versionName,
                isDebug = buildInfo.isDebug,
                onQaOptionClicked = navController::navigateToQa
            )
        }
    }
}
