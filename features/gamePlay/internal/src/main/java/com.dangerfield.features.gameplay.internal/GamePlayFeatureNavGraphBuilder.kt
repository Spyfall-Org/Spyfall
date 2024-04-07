package com.dangerfield.features.gameplay.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dangerfield.features.gameplay.gamePlayScreenRoute
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action.LoadGamePlay
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action.SubmitLocationVote
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Action.SubmitOddOneOutVote
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Event.GameKilled
import com.dangerfield.features.gameplay.internal.GamePlayViewModel.Event.GameReset
import com.dangerfield.features.gameplay.internal.ui.GamePlayScreen
import com.dangerfield.features.gameplay.internal.voting.VotingBottomSheet
import com.dangerfield.features.gameplay.internal.voting.hasVotedArgument
import com.dangerfield.features.gameplay.internal.voting.navigateToVotingInfo
import com.dangerfield.features.gameplay.internal.voting.votingInfoRoute
import com.dangerfield.features.rules.navigateToRules
import com.dangerfield.features.videoCall.navigateToVideoCallBottomSheet
import com.dangerfield.features.waitingroom.navigateToWaitingRoom
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.floatingwindow.bottomSheet
import com.dangerfield.libraries.navigation.navArgument
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class GamePlayFeatureNavGraphBuilder @Inject constructor(
) : FeatureNavBuilder {
    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        composable(
            route = gamePlayScreenRoute.navRoute,
            arguments = gamePlayScreenRoute.navArguments
        ) {

            val viewModel = hiltViewModel<GamePlayViewModel>()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()
            val context = LocalContext.current

            PageLogEffect(
                route = gamePlayScreenRoute,
                type = PageType.FullScreenPage
            )

            ObserveWithLifecycle(flow = viewModel.eventFlow) {
                when (it) {
                    is GameReset -> router.navigateToWaitingRoom(accessCode = it.accessCode)
                    GameKilled -> router.popBackTo(welcomeNavigationRoute)
                }
            }

            LaunchedEffect(Unit) {
                viewModel.takeAction(LoadGamePlay)
            }

            GamePlayScreen(
                players = state.players,
                locations = state.locations,
                timeRemaining = state.timeRemainingMillis.millisToMMss(),
                role = state.mePlayer?.role.orEmpty(),
                isOddOneOut = state.mePlayer?.isOddOneOut ?: false,
                isTimeUp = state.isTimeUp,
                onVotingQuestionClicked = router::navigateToVotingInfo,
                isLoadingVote = state.isLoadingVoteSubmit,
                onGamePlayQuestionClicked = router::navigateToRules,
                onSubmitPlayerVoteClicked = { viewModel.takeAction(SubmitOddOneOutVote(id = it)) },
                onSubmitLocationVoteClicked = { viewModel.takeAction(SubmitLocationVote(location = it)) },
                location = state.location,
                hasMePlayerSubmittedVote = state.isVoteSubmitted,
                gameResult = state.gameResult,
                onTimeToVote = {
                    playDingSound(context)
                    router.navigateToVotingInfo()
                },
                onResetGameClicked = {
                    viewModel.takeAction(GamePlayViewModel.Action.ResetGame)
                },
                onEndGameClicked = { viewModel.takeAction(GamePlayViewModel.Action.EndGame) },
                videoCallLink = state.videoCallLink,
                onVideoCallButtonClicked = router::navigateToVideoCallBottomSheet,
                mePlayerId = state.mePlayer?.id.orEmpty(),
                canControlGame = state.canControlGame
            )
        }

        bottomSheet(
            route = votingInfoRoute.navRoute,
            arguments = votingInfoRoute.navArguments
        ) {
            val hasVoted = it.navArgument<Boolean>(hasVotedArgument) ?: false

            PageLogEffect(
                route = votingInfoRoute,
                type = PageType.BottomSheet,
                extras = bundleOf(
                    "hasVoted" to hasVoted.toString()
                )
            )

            VotingBottomSheet(
                onDismiss = router::dismissSheet,
                hasVoted = hasVoted
            )
        }
    }
}