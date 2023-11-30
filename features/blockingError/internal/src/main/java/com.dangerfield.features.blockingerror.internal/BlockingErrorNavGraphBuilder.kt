package com.dangerfield.features.blockingerror.internal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.blockingerror.blockingErrorBaseRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class BlockingErrorNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = blockingErrorBaseRoute,
        ) {
            BlockingErrorScreen()
        }
    }
}
