package com.dangerfield.features.blockingerror.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.blockingerror.blockingErrorRoute
import com.dangerfield.features.blockingerror.maintenanceRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.route
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class BlockingErrorNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = blockingErrorRoute.navRoute,
            arguments = blockingErrorRoute.navArguments
        ) {

            PageLogEffect(
                route = blockingErrorRoute,
                type = PageType.FullScreenPage
            )

            BlockingErrorScreen()
        }

        composable(
            route = maintenanceRoute.navRoute,
            arguments = maintenanceRoute.navArguments
        ) {

            PageLogEffect(
                route = maintenanceRoute,
                type = PageType.FullScreenPage
            )

            MaintenanceModeScreen()
        }
    }
}
