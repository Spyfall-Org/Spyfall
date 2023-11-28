package com.dangerfield.features.newgame.internal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.newgame.internal.NewGameViewModel.GameType.SingleDevice
import com.dangerfield.features.newgame.newGameNavigationRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.ui.ModuleNavBuilder
import javax.inject.Inject

@AutoBindIntoSet
class NewGameModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {
        composable(
            route = newGameNavigationRoute,
        ) {

            val viewModel = hiltViewModel<NewGameViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            NewGameScreen(
                packs = state.packs,
                onPackSelected = viewModel::selectPack,
                name = state.name,
                onNameUpdated = viewModel::updateName,
                timeLimit = state.timeLimit,
                maxPlayers = state.maxPlayers,
                onTimeLimitUpdated = viewModel::updateTimeLimit,
                isSingleDevice = state.gameType is SingleDevice,
                onIsSingleDeviceUpdated = viewModel::updateGameType,
                numOfPlayers = (state.gameType as? SingleDevice)?.numberOfPlayers.orEmpty(),
                onNumOfPlayersUpdated = viewModel::updateNumOfPlayers
            )
        }
    }
}
