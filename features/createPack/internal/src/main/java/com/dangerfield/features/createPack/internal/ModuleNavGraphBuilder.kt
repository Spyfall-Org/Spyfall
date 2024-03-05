package com.dangerfield.features.createPack.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.createPack.createPackRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import javax.inject.Inject

@AutoBindIntoSet
class ModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = createPackRoute.navRoute,
            arguments = createPackRoute.navArguments
        ) {
            // TODO - add your feature composable here
        }
    }
}