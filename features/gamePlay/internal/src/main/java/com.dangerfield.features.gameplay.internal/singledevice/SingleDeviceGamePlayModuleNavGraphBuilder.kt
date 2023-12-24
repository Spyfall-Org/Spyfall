package com.dangerfield.features.gameplay.internal.singledevice

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.navigateToSingleDevicePlayerRoleRoute
import com.dangerfield.features.gameplay.internal.singleDevicePlayerRoleRoute
import com.dangerfield.features.gameplay.internal.singledevice.SingleDevicePlayerRoleViewModel.Action.LoadGame
import com.dangerfield.features.gameplay.internal.singledevice.SingleDevicePlayerRoleViewModel.Action.LoadNextPlayer
import com.dangerfield.features.gameplay.internal.singledevice.SingleDevicePlayerRoleViewModel.Action.StartGame
import com.dangerfield.features.gameplay.internal.singledevice.SingleDevicePlayerRoleViewModel.Action.UpdateName
import com.dangerfield.features.gameplay.internal.voting.hasVotedArgument
import com.dangerfield.features.gameplay.singleDeviceInfoRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.navArgument
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.developerSnackIfDebug
import spyfallx.core.doNothing
import javax.inject.Inject

@AutoBindIntoSet
class SingleDeviceGamePlayModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        composable(
            route = singleDeviceInfoRoute.navRoute,
            arguments = singleDeviceInfoRoute.navArguments
        ) {
            val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable

            SingleDeviceInfoScreen(
                onStartClicked = {
                    router.navigateToSingleDevicePlayerRoleRoute(
                        accessCode = accessCode
                    )
                }
            )
        }

        composable(
            route = singleDevicePlayerRoleRoute.navRoute,
            arguments = singleDevicePlayerRoleRoute.navArguments
        ) {

            val viewModel = hiltViewModel<SingleDevicePlayerRoleViewModel>()

            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state.isGameStarted) {
                if (state.isGameStarted) {
                    developerSnackIfDebug {
                        "Game started, navigate to game play screen"
                    }
                }
            }

            LaunchedEffect(Unit) {
                developerSnackIfDebug {
                    "sending load"
                }
                viewModel.takeAction(LoadGame)
            }

            SingleDevicePlayerRoleScreen(
                currentPlayer = state.currentPlayer,
                location = state.location,
                onNextPlayerClicked = { viewModel.takeAction(LoadNextPlayer) },
                isLastPlayer = state.isLastPlayer,
                onStartGameClicked = { viewModel.takeAction(StartGame) },
                nameFieldState = state.nameFieldState,
                onNameUpdated = { viewModel.takeAction(UpdateName(it))},
            )
        }
    }
}
