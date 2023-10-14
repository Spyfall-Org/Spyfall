package com.dangerfield.features.joingame.internal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.joingame.joinGameNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.coreui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class JoinGameModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = joinGameNavigationRoute,
        ) {
            JoinGameScreen()
        }
    }
}