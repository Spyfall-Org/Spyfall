package com.dangerfield.features.newgame.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.navigateToSingleDeviceInfoRoute
import com.dangerfield.features.newgame.internal.presentation.NewGameOfflineScreen
import com.dangerfield.features.newgame.internal.presentation.NewGameScreen
import com.dangerfield.features.newgame.internal.presentation.NewGameViewModel
import com.dangerfield.features.newgame.internal.presentation.model.Event
import com.dangerfield.features.newgame.internal.presentation.model.FormState
import com.dangerfield.features.newgame.newGameNavigationRoute
import com.dangerfield.features.videoCall.navigateToVideoCallLinkInfo
import com.dangerfield.features.waitingroom.navigateToWaitingRoom
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.LocalAppState
import se.ansman.dagger.auto.AutoBindIntoSet
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AutoBindIntoSet
class NewGameModuleNavGraphBuilder @Inject constructor(
    private val gameConfig: GameConfig,
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = newGameNavigationRoute.navRoute,
            arguments = newGameNavigationRoute.navArguments,
        ) {

            val appState = LocalAppState.current
            val viewModel = hiltViewModel<NewGameViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val isOffline by appState.isOffline.collectAsStateWithLifecycle()
            val hasLoaded = AtomicBoolean(false)


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

            LaunchedEffect(isOffline) {
                if (!isOffline && !hasLoaded.getAndSet(true)) {
                    viewModel.load()
                }
            }

            if (isOffline) {
                NewGameOfflineScreen(
                    onNavigateBack = router::goBack,
                )
            } else {
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
                    didLoadFail = state.didLoadFail,
                    didCreationFail = state.didCreationFail,
                    onCreateGameClicked = viewModel::createGame,
                    maxGameLength = gameConfig.maxTimeLimit,
                    minGameLength = gameConfig.minTimeLimit,
                    packsState = state.packsState,
                    nameState = state.nameState,
                    timeLimitState = state.timeLimitState,
                    numOfPlayersState = state.numberOfPlayersState,
                    isFormValid = state.formState is FormState.Valid,
                    isSingleDeviceModeEnabled = gameConfig.isSingleDeviceModeEnabled,
                    isVideoCallLinkEnabled = gameConfig.isVideoCallLinkEnabled,
                    onVideoCallLinkInfoClicked = router::navigateToVideoCallLinkInfo,
                    onErrorDismissed = viewModel::resolveSomethingWentWrong,
                    )
            }
        }
    }
}
