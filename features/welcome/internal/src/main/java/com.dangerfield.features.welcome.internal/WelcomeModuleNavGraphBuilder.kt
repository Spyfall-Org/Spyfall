package com.dangerfield.features.welcome.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.navigateToGamePlayScreen
import com.dangerfield.features.joingame.navigateToJoinGame
import com.dangerfield.features.newgame.navigateToNewGame
import com.dangerfield.features.rules.navigateToRules
import com.dangerfield.features.settings.navigateToSettings
import com.dangerfield.features.waitingroom.navigateToWaitingRoom
import com.dangerfield.features.welcome.internal.WelcomeViewModel.Action.CheckForActiveGame
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class WelcomeModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    private fun Router.isOnWelcomeScreen() = currentRouteName == welcomeNavigationRoute.navRoute

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = welcomeNavigationRoute.navRoute,
            arguments = welcomeNavigationRoute.navArguments
        ) {

            val viewModel: WelcomeViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                viewModel.takeAction(CheckForActiveGame)
            }

            ObserveWithLifecycle(flow = viewModel.events) {
                when (it) {
                    is WelcomeViewModel.Event.GameInProgressFound -> {
                        if (router.isOnWelcomeScreen()) {
                            router.navigateToGamePlayScreen(
                                accessCode = it.accessCode, timeLimit = null
                            )
                        }
                    }

                    is WelcomeViewModel.Event.GameInWaitingRoomFound -> {
                        if (router.isOnWelcomeScreen()) {
                            router.navigateToWaitingRoom(it.accessCode)
                        }
                    }
                }
            }

            WelcomeScreen(
                onJoinGameClicked = router::navigateToJoinGame,
                onNewGameClicked = router::navigateToNewGame,
                onSettingsClicked = router::navigateToSettings,
                onRulesClicked = router::navigateToRules
            )
        }
    }
}
