package com.dangerfield.features.settings.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.qa.navigateToQa
import com.dangerfield.features.settings.settingsNavigationRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.BuildInfo
import javax.inject.Inject

@AutoBindIntoSet
class SettingsModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo
): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = settingsNavigationRoute.navRoute,
            arguments = settingsNavigationRoute.navArguments,
        ) {
            SettingsScreen(
                versionName = buildInfo.versionName,
                isDebug = buildInfo.isDebug,
                onQaOptionClicked = router::navigateToQa,
                onNavigateBack = router::goBack
            )
        }
    }
}
