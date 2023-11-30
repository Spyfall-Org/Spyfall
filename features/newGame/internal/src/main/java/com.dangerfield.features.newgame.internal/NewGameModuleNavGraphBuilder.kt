package com.dangerfield.features.newgame.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.dangerfield.features.newgame.internal.presentation.model.Event
import com.dangerfield.features.newgame.internal.presentation.NewGameScreen
import com.dangerfield.features.newgame.internal.presentation.NewGameViewModel
import com.dangerfield.features.newgame.internal.presentation.VideoCallLinkInfoDialog
import com.dangerfield.features.newgame.internal.presentation.model.FormState
import com.dangerfield.features.newgame.internal.usecase.RecognizedVideoCallingPlatforms
import com.dangerfield.features.newgame.newGameNavigationRoute
import com.dangerfield.features.waitingroom.navigateToWaitingRoom
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.rawRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class NewGameModuleNavGraphBuilder @Inject constructor(
    private val gameConfig: GameConfig,
    private val recognizedVideoCallingPlatforms: RecognizedVideoCallingPlatforms
) : ModuleNavBuilder {

    // TODO consider making a router and asserting more control over navigation
    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = newGameNavigationRoute,
        ) {

            val viewModel = hiltViewModel<NewGameViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            ObserveWithLifecycle(flow = viewModel.events) {
                when (it) {
                    is Event.GameCreated -> navController.navigateToWaitingRoom(
                        accessCode = it.accessCode,
                        videoCallLink = it.videoCallLink,
                    )

                    is Event.SingleDeviceGameCreated -> {
                        // TODO navigate to singleDeviceGame feature, or just straight to game?
                        // or should these also be in the waiting room?
                        // idk man
                    }
                }
            }

            NewGameScreen(
                onPackSelected = viewModel::selectPack,
                onNameUpdated = viewModel::updateName,
                maxPlayers = gameConfig.maxPlayers,
                minPlayers = gameConfig.minPlayers,
                onTimeLimitUpdated = viewModel::updateTimeLimit,
                isSingleDevice = state.isSingleDevice,
                onIsSingleDeviceUpdated = viewModel::updateGameType,
                onNumOfPlayersUpdated = viewModel::updateNumOfPlayers,
                videoCallLinkState = state.videoCallLinkState,
                onVideoCallLinkUpdated = viewModel::updateVideoCallLink,
                onNavigateBack = navController::popBackStack,
                didSomethingGoWrong = state.didSomethingGoWrong,
                onCreateGameClicked = viewModel::createGame,
                maxGameLength = gameConfig.maxTimeLimit,
                minGameLength = gameConfig.minTimeLimit,
                packsState = state.packsState,
                nameState = state.nameState,
                timeLimitState = state.timeLimitState,
                numOfPlayersState = state.numberOfPlayersState,
                isFormValid = state.formState is FormState.Valid,
                isSingleDeviceModeEnabled = gameConfig.isSingleDeviceModeEnabled,
                isVideoCallLinkEnabled = recognizedVideoCallingPlatforms().isNotEmpty(),
                onVideoCallLinkInfoClicked = navController::navigateToVideoCallLinkInfo,
            )
        }

        dialog(
            route = videoCallLinkInfoRoute.rawRoute(),
        ) {
            VideoCallLinkInfoDialog(
                recognizedPlatforms = recognizedVideoCallingPlatforms().keys.toList(),
                onDismiss = navController::popBackStack,
            )
        }
    }
}
