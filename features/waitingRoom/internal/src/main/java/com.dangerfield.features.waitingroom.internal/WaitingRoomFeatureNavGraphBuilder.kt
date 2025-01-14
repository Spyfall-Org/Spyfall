package com.dangerfield.features.waitingroom.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.navigateToGamePlayScreen
import com.dangerfield.features.rules.navigateToRules
import com.dangerfield.features.videoCall.navigateToVideoCallBottomSheet
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action.LeaveGame
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action.LoadRoom
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.Action.StartGame
import com.dangerfield.features.waitingroom.internal.changename.ChangeNameDialog
import com.dangerfield.features.waitingroom.internal.changename.ChangeNameViewModel
import com.dangerfield.features.waitingroom.internal.changename.changeNameRoute
import com.dangerfield.features.waitingroom.internal.changename.navigateToChangeName
import com.dangerfield.features.waitingroom.waitingRoomRoute
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.dialog
import com.dangerfield.libraries.ui.showMessage
import com.dangerfield.oddoneoout.features.waitingroom.internal.R
import oddoneout.core.Message
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class WaitingRoomFeatureNavGraphBuilder @Inject constructor(
    private val gameConfig: GameConfig
) : FeatureNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        composable(
            route = waitingRoomRoute.navRoute,
            arguments = waitingRoomRoute.navArguments
        ) {

            val viewModel = hiltViewModel<WaitingRoomViewModel>()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()
            val message = dictionaryString(R.string.waitingRoom_cannotLeaveGame_text)

            PageLogEffect(
                route = waitingRoomRoute,
                type = PageType.FullScreenPage
            )

            ObserveWithLifecycle(flow = viewModel.eventFlow) { event ->
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
                                message,
                                autoDismiss = true
                            )
                        )
                    }

                    WaitingRoomViewModel.Event.LeftGame -> router.popBackTo(welcomeNavigationRoute)
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
                onCallLinkButtonClicked = router::navigateToVideoCallBottomSheet,
                onLeaveGameClicked = { viewModel.takeAction(LeaveGame) },
                onHelpClicked = router::navigateToRules,
                onChangeNameClicked = { router.navigateToChangeName(state.accessCode) },
                minPlayers = gameConfig.minPlayers,
                maxPlayers = gameConfig.maxPlayers,
                canControlGame = state.canControlGame
            )
        }

        dialog(
            route = changeNameRoute.navRoute,
            arguments = changeNameRoute.navArguments
        ) {
            val viewModel = hiltViewModel<ChangeNameViewModel>()

            val state by viewModel.stateFlow.collectAsStateWithLifecycle()

            PageLogEffect(
                route = changeNameRoute,
                type = PageType.Dialog
            )

            ObserveWithLifecycle(flow = viewModel.eventFlow) { event ->
                when (event) {
                    is ChangeNameViewModel.Event.NameChanged -> router.goBack()
                }
            }

            ChangeNameDialog(
                name = state.name,
                onNameUpdated = { viewModel.takeAction(ChangeNameViewModel.Action.UpdateName(it)) },
                onChangeNameClicked = { viewModel.takeAction(ChangeNameViewModel.Action.SubmitNameChange(it)) },
                minNameLength = state.minNameLength,
                maxNameLength = state.maxNameLength,
                isNameTaken = state.isNameTaken,
                isLoading = state.isLoading,
                isInvalidLength = state.isNameInvalidLength,
                onDismissRequest =  router::goBack
            )
        }
    }
}
