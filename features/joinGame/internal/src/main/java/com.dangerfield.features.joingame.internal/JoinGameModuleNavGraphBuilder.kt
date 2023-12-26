package com.dangerfield.features.joingame.internal

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.joingame.joinGameNavigationRoute
import com.dangerfield.features.waitingroom.navigateToWaitingRoom
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.BuildInfo
import spyfallx.core.openStoreLinkToApp
import javax.inject.Inject

@AutoBindIntoSet
class JoinGameModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = joinGameNavigationRoute.navRoute,
            arguments = joinGameNavigationRoute.navArguments
        ) {
            val viewModel = hiltViewModel<JoinGameViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current

            ObserveWithLifecycle(flow = viewModel.events) {
                when (it) {
                    is Event.GameJoined -> {
                        router.navigateToWaitingRoom(it.accessCode)
                    }
                }
            }

            JoinGameScreen(
                accessCode = state.accessCodeState.value,
                userName = state.userNameState.value,
                isLoading = state.isLoading,
                gameNotFound = state.accessCodeState.gameDoesNotExist,
                unresolvableError = state.unresolvableError,
                invalidNameLengthError = state.userNameState.invalidNameLengthError,
                gameAlreadyStarted = state.accessCodeState.gameAlreadyStarted,
                invalidAccessCodeLengthError = state.accessCodeState.invalidLengthError,
                usernameTaken = state.userNameState.isTaken,
                onJoinGameClicked = viewModel::joinGame,
                onAccessCodeChanged = viewModel::updateAccessCode,
                onUserNameChanged = viewModel::updateUserName,
                onSomethingWentWrongDismissed = viewModel::onSomethingWentWrongDismissed,
                onUpdateAppClicked = { context.openStoreLinkToApp(buildInfo) },
                onNavigateBack = router::goBack,
            )
        }
    }
}