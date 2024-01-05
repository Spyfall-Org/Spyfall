package com.dangerfield.features.newgame.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.navigateToSingleDeviceInfoRoute
import com.dangerfield.features.newgame.internal.presentation.NewGameScreen
import com.dangerfield.features.newgame.internal.presentation.NewGameViewModel
import com.dangerfield.features.newgame.internal.presentation.model.Event
import com.dangerfield.features.newgame.internal.presentation.model.FormState
import com.dangerfield.features.newgame.newGameNavigationRoute
import com.dangerfield.features.videoCall.IsVideoCallingEnabled
import com.dangerfield.features.videoCall.navigateToVideoCallLinkInfo
import com.dangerfield.features.waitingroom.navigateToWaitingRoom
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class NewGameModuleNavGraphBuilder @Inject constructor(
    private val gameConfig: GameConfig,
    private val isVideoCallingEnabled: IsVideoCallingEnabled,
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = newGameNavigationRoute.navRoute,
            arguments = newGameNavigationRoute.navArguments,
        ) {

            val viewModel = hiltViewModel<NewGameViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            ObserveWithLifecycle(flow = viewModel.events) {
                when (it) {
                    is Event.GameCreated -> router.navigateToWaitingRoom(
                        accessCode = it.accessCode,
                    )

                    is Event.SingleDeviceGameCreated -> {
                        router.navigateToSingleDeviceInfoRoute(
                            accessCode = it.accessCode,
                        )
                    }
                }
            }

            NewGameScreen(
                onPackSelected = viewModel::selectPack,
                onNameUpdated = viewModel::updateName,
                maxPlayers = gameConfig.maxPlayers,
                minPlayers = gameConfig.minPlayers,
                onTimeLimitUpdated = viewModel::updateTimeLimit,
                isLoadingCreation = state.isLoadingCreation,
                isLoadingPacks = state.isLoadingPacks,
                isSingleDevice = state.isSingleDevice,
                onIsSingleDeviceUpdated = viewModel::updateGameType,
                onNumOfPlayersUpdated = viewModel::updateNumOfPlayers,
                videoCallLinkState = state.videoCallLinkState,
                onVideoCallLinkUpdated = viewModel::updateVideoCallLink,
                onNavigateBack = router::goBack,
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
                isVideoCallLinkEnabled = isVideoCallingEnabled(),
                onVideoCallLinkInfoClicked = router::navigateToVideoCallLinkInfo
            )
        }
    }
}
