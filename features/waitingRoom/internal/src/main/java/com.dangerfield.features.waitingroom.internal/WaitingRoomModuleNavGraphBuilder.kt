package com.dangerfield.features.waitingroom.internal

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.waitingroom.accessCodeArgument
import com.dangerfield.features.waitingroom.waitingRoomRoute
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.rawRoute
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class WaitingRoomModuleNavGraphBuilder @Inject constructor(): ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(navController: NavController) {

        composable(
            route = waitingRoomRoute.rawRoute(),
            arguments = listOf(accessCodeArgument)
        ) {

            val viewModel = hiltViewModel<WaitingRoomViewModel>()

            val state by viewModel.state.collectAsStateWithLifecycle()

            WaitingRoomScreen(
                accessCode = state.accessCode,
                players = state.players,
                isLoadingRoom = state.isLoadingRoom,
                isLoadingStart = state.isLoadingStart,
                videoCallLink = state.videoCallLink,
                onCallLinkButtonClicked = {
                    /*
                    show a pop up modal to open or copy link
                     */
                }
            )
        }
    }
}
