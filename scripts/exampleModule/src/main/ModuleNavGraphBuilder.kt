package com.dangerfield.features.example.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.example.exampleRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.ui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = exampleRoute.navRoute,
            arguments = exampleRoute.navArguments
        ) {
            // TODO - add your feature composable here
        }
    }
}