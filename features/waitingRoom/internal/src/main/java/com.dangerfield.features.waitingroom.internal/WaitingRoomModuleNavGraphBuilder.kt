package com.dangerfield.features.waitingroom.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.navigateToGamePlayScreen
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action.LeaveGame
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action.LoadRoom
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action.StartGame
import com.dangerfield.features.waitingroom.waitingRoomRoute
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.showMessage
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.Message
import javax.inject.Inject

@AutoBindIntoSet
class WaitingRoomModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        composable(
            route = waitingRoomRoute.navRoute,
            arguments = waitingRoomRoute.navArguments
        ) {

            val viewModel = hiltViewModel<WaitingRoomViewModel>()

            val state by viewModel.state.collectAsStateWithLifecycle()

            ObserveWithLifecycle(flow = viewModel.events) { event ->
                when (event) {
                    is WaitingRoomViewModel.Event.GameStarted -> {
                        router.navigateToGamePlayScreen(
                            accessCode = event.accessCode,
                            timeLimit = event.timeLimit
                        )
                    }

                    WaitingRoomViewModel.Event.TriedToLeaveStartedGame -> {
                        showMessage(
                            message = Message(
                                "Cannot Leave a started game",
                                autoDismiss = true
                            )
                        )
                    }

                    WaitingRoomViewModel.Event.LeftGame -> router.goBack()
                }
            }

            LaunchedEffect(Unit) {
                viewModel.takeAction(LoadRoom)
            }

            WaitingRoomScreen(
                accessCode = state.accessCode,
                players = state.players,
                isLoadingRoom = state.isLoadingRoom,
                isLoadingStart = state.isLoadingStart,
                videoCallLink = state.videoCallLink,
                onStartGameClicked = { viewModel.takeAction(StartGame) },
                onCallLinkButtonClicked = {
                    /*
                    show a pop up modal to open or copy link
                     */
                },
                onLeaveGameClicked = { viewModel.takeAction(LeaveGame) }
            )
        }
    }
}
