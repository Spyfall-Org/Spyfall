package com.dangerfield.features.newgame.internal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.newgame.newGameNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.coreui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class NewGameModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = newGameNavigationRoute,
        ) {
            NewGameScreen()
        }
    }
}
