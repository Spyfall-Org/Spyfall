package com.dangerfield.features.forcedupdate.internal

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.BuildInfo
import spyfallx.core.openStoreLinkToApp
import javax.inject.Inject

@AutoBindIntoSet
class ForcedUpdateModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo
): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = forcedUpdateNavigationRoute,
        ) {
            val context = LocalContext.current
            ForcedUpdateScreen(
                onOpenAppStoreClicked = {
                    context.openStoreLinkToApp(buildInfo)
                }
            )
        }
    }
}
