package com.dangerfield.features.gameplay.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.gamePlayScreenRoute
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action.LoadGamePlay
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action.SubmitLocationVote
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action.SubmitOddOneOutVote
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.bottomsheet.bottomSheet
import se.ansman.dagger.auto.AutoBindIntoSet
import spyfallx.core.doNothing
import javax.inject.Inject

@AutoBindIntoSet
class GamePlayModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        composable(
            route = gamePlayScreenRoute.navRoute,
            arguments = gamePlayScreenRoute.navArguments
        ) {

            val viewModel = hiltViewModel<GamePlayViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            ObserveWithLifecycle(flow = viewModel.events) {
                when (it) {
                    GamePlayViewModel.Event.GameEnded -> doNothing()
                    GamePlayViewModel.Event.GameTimedOut -> router.navigateToVotingInfo()
                    GamePlayViewModel.Event.GameReset -> doNothing()
                }
            }

            LaunchedEffect(Unit) {
                viewModel.takeAction(LoadGamePlay)
            }

            GamePlayScreen(
                players = state.players,
                locations = state.locations,
                timeRemaining = state.timeRemaining,
                role = state.mePlayer?.role.orEmpty(),
                isOddOneOut = state.mePlayer?.isOddOneOut ?: false,
                isTimeUp = state.isTimeUp,
                onVotingQuestionClicked = router::navigateToVotingInfo,
                isLoadingVote = state.isLoadingVoteSubmit,
                onGamePlayQuestionClicked = router::navigateToGameHelp,
                onSubmitPlayerVoteClicked = { viewModel.takeAction(SubmitOddOneOutVote(id = it)) },
                onSubmitLocationVoteClicked = { viewModel.takeAction(SubmitLocationVote(location = it)) },
                location = state.location,
                hasMePlayerSubmittedVote = state.isVoteSubmitted,
                gameResult = state.gameResult,
            )
        }

        bottomSheet(
            route = votingRoute.navRoute,
            arguments = votingRoute.navArguments
        ) {
            VotingBottomSheet(
                onDismiss = router::dismissSheet
            )
        }

        bottomSheet(
            route = gameHelpRoute.navRoute,
            arguments = gameHelpRoute.navArguments
        ) {
            GameHelpBottomSheet(
                onDismiss = router::dismissSheet
            )
        }
    }
}