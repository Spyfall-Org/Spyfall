package com.dangerfield.features.joingame.internal

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.joingame.joinGameNavigationRoute
import com.dangerfield.features.waitingroom.navigateToWaitingRoom
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.BuildInfo
import spyfallx.core.openStoreLinkToApp
import javax.inject.Inject

@AutoBindIntoSet
class JoinGameModuleNavGraphBuilder @Inject constructor(
    private val buildInfo: BuildInfo,
    private val gameConfig: GameConfig
) : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {
        composable(
            route = joinGameNavigationRoute.navRoute,
            arguments = joinGameNavigationRoute.navArguments
        ) {
            val viewModel = hiltViewModel<JoinGameViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current

            PageLogEffect(
                route = joinGameNavigationRoute,
                type = PageType.FullScreenPage
            )

            ObserveWithLifecycle(flow = viewModel.events) {
                when (it) {
                    is Event.GameJoined -> {
                        router.navigateToWaitingRoom(it.accessCode)
                    }
                }
            }

            JoinGameScreen(
                isLoading = state.isLoading,
                unresolvableError = state.unresolvableError,
                onJoinGameClicked = viewModel::joinGame,
                onAccessCodeChanged = viewModel::updateAccessCode,
                onUserNameChanged = viewModel::updateUserName,
                onSomethingWentWrongDismissed = viewModel::onSomethingWentWrongDismissed,
                onUpdateAppClicked = { context.openStoreLinkToApp(buildInfo) },
                onNavigateBack = router::goBack,
                accessCodeState = state.accessCodeState,
                userNameState = state.userNameState,
                isFormValid = state.isFormValid,
                accessCodeLength = gameConfig.accessCodeLength
            )
        }
    }
}
