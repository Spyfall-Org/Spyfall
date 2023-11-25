package com.dangerfield.features.welcome.internal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.joingame.navigateToJoinGame
import com.dangerfield.features.newgame.navigateToNewGame
import com.dangerfield.features.settings.navigateToSettings
import com.dangerfield.features.welcome.welcomeNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.ui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class WelcomeModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = welcomeNavigationRoute,
        ) {
            WelcomeScreen(
                onJoinGameClicked = navController::navigateToJoinGame,
                onNewGameClicked = navController::navigateToNewGame,
                onSettingsClicked = navController::navigateToSettings
            )
        }
    }
}
