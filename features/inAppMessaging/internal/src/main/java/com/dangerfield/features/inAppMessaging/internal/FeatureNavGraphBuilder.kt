package com.dangerfield.features.inAppMessaging.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.inAppMessaging.inAppMessagingRoute
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class FeatureNavGraphBuilder @Inject constructor(): FeatureNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = inAppMessagingRoute.navRoute,
            arguments = inAppMessagingRoute.navArguments
        ) {
            // TODO - add your feature composable here
        }
    }
}