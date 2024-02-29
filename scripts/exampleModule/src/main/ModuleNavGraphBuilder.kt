package com.dangerfield.features.example.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.example.exampleRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
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