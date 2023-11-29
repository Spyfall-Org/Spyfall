package com.dangerfield.features.newgame.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.newgame.internal.presentation.model.Event
import com.dangerfield.features.newgame.internal.presentation.model.GameType
import com.dangerfield.features.newgame.internal.presentation.NewGameScreen
import com.dangerfield.features.newgame.internal.presentation.NewGameViewModel
import com.dangerfield.features.newgame.internal.presentation.model.FormState
import com.dangerfield.features.newgame.newGameNavigationRoute
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameConfig
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.ui.ModuleNavBuilder
import timber.log.Timber
import javax.inject.Inject

@AutoBindIntoSet
class NewGameModuleNavGraphBuilder @Inject constructor(
    private val gameConfig: GameConfig,
    ) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = newGameNavigationRoute,
        ) {

            val viewModel = hiltViewModel<NewGameViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            ObserveWithLifecycle(flow = viewModel.events) {
                when (it) {
                    is Event.GameCreated -> {
                        Timber.d("Game Created!")
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
                videoCallLink = state.videoCallLink,
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
            )
        }
    }
}
