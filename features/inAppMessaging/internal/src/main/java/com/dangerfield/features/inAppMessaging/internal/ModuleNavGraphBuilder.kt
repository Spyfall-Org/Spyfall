package com.dangerfield.features.inAppMessaging.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.inAppMessaging.inAppMessagingRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = inAppMessagingRoute.navRoute,
            arguments = inAppMessagingRoute.navArguments
        ) {
            // TODO - add your feature composable here
        }
    }
}