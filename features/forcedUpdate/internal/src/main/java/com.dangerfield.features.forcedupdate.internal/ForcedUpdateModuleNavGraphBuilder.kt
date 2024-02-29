package com.dangerfield.features.forcedupdate.internal

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.forcedupdate.forcedUpdateNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import oddoneout.core.BuildInfo
import oddoneout.core.openStoreLinkToApp
import javax.inject.Inject

@AutoBindIntoSet
class ForcedUpdateModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo
): ModuleNavBuilder {


    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = forcedUpdateNavigationRoute.navRoute,
            arguments = forcedUpdateNavigationRoute.navArguments,
        ) {
            val context = LocalContext.current

            PageLogEffect(
                route = forcedUpdateNavigationRoute,
                type = PageType.FullScreenPage
            )

            ForcedUpdateScreen(
                onOpenAppStoreClicked = {
                    context.openStoreLinkToApp(buildInfo)
                }
            )
        }
    }
}
