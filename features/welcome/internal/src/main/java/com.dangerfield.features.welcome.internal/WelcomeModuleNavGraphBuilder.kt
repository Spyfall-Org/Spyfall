package com.dangerfield.features.welcome.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.welcome.welcomeNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.coreui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class WelcomeModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {
    override fun NavGraphBuilder.buildNavGraph() {
        composable(
            route = welcomeNavigationRoute,
        ) {
            WelcomeScreen()
        }
    }
}