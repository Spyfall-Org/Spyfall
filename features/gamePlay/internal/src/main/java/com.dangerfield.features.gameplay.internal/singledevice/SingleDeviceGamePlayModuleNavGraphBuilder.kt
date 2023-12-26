package com.dangerfield.features.gameplay.internal.singledevice

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.navigateToSingleDevicePlayerRoleRoute
import com.dangerfield.features.gameplay.internal.singleDevicePlayerRoleRoute
import com.dangerfield.features.gameplay.internal.singleDeviceVotingNavigationRoute
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayScreen
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Event.GameKilled
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Event.GameReset
import com.dangerfield.features.gameplay.internal.singledevice.results.SingleDeviceResultsScreen
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDevicePlayerRoleScreen
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.LoadGame
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.LoadNextPlayer
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.StartGame
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.UpdateName
import com.dangerfield.features.gameplay.internal.singledevice.voting.SingleDeviceVotingScreen
import com.dangerfield.features.gameplay.internal.singledevice.voting.SingleDeviceVotingViewModel
import com.dangerfield.features.gameplay.internal.singledevice.voting.SingleDeviceVotingViewModel.Action.SubmitVoteForPlayer
import com.dangerfield.features.gameplay.internal.voting.navigateToSingleDeviceVoting
import com.dangerfield.features.gameplay.internal.voting.navigateToSingleDeviceVotingResults
import com.dangerfield.features.gameplay.internal.voting.navigateToVotingInfo
import com.dangerfield.features.gameplay.internal.voting.singleDeviceVotingResultsRoute
import com.dangerfield.features.gameplay.internal.voting.singleDeviceVotingRoute
import com.dangerfield.features.gameplay.navigateToSingleDeviceGamePlayScreen
import com.dangerfield.features.gameplay.singleDeviceGamePlayRoute
import com.dangerfield.features.gameplay.singleDeviceInfoRoute
import com.dangerfield.features.gameplay.timeLimitArgument
import com.dangerfield.features.welcome.welcomeNavigationRoute
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.navigation.ModuleNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.navigation.viewModelScopedTo
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class SingleDeviceGamePlayModuleNavGraphBuilder @Inject constructor() : ModuleNavBuilder {

    @Suppress("LongMethod")
    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        composable(
            route = singleDeviceInfoRoute.navRoute,
            arguments = singleDeviceInfoRoute.navArguments
        ) {
            val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable
            val timeLimit = it.navArgument<Int>(timeLimitArgument) ?: return@composable

            SingleDeviceInfoScreen(
                onStartClicked = {
                    router.navigateToSingleDevicePlayerRoleRoute(
                        accessCode = accessCode,
                        timeLimit = timeLimit
                    )
                }
            )
        }

        composable(
            route = singleDevicePlayerRoleRoute.navRoute,
            arguments = singleDevicePlayerRoleRoute.navArguments
        ) {

            val viewModel = hiltViewModel<SingleDeviceRoleRevealViewModel>()

            val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable
            val timeLimit = it.navArgument<Int>(timeLimitArgument) ?: return@composable

            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state.isGameStarted) {
                if (state.isGameStarted) {
                    router.navigateToSingleDeviceGamePlayScreen(
                        accessCode = accessCode,
                        timeLimit = timeLimit
                    )
                }
            }

            LaunchedEffect(Unit) {
                viewModel.takeAction(LoadGame)
            }

            SingleDevicePlayerRoleScreen(
                currentPlayer = state.currentPlayer,
                location = state.location,
                onNextPlayerClicked = { viewModel.takeAction(LoadNextPlayer) },
                isLastPlayer = state.isLastPlayer,
                onStartGameClicked = { viewModel.takeAction(StartGame) },
                nameFieldState = state.nameFieldState,
                onNameUpdated = { viewModel.takeAction(UpdateName(it)) },
                locationOptions = state.locationOptions,
            )
        }

        composable(
            route = singleDeviceGamePlayRoute.navRoute,
            arguments = singleDeviceGamePlayRoute.navArguments
        ) {
            val viewModel = hiltViewModel<SingleDeviceGamePlayViewModel>()

            val state by viewModel.state.collectAsStateWithLifecycle()

            val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable

            ObserveWithLifecycle(flow = viewModel.events) { event ->
                when (event) {
                    is GameReset -> router.popBackTo(singleDeviceInfoRoute)
                    GameKilled -> router.popBackTo(welcomeNavigationRoute)
                }
            }

            LaunchedEffect(Unit) {
                viewModel.takeAction(SingleDeviceGamePlayViewModel.Action.LoadGame)
            }

            // TODO resuming on voting ended stage for some reason isnt working

            SingleDeviceGamePlayScreen(
                timeRemaining = state.timeRemaining,
                isTimeUp = state.isTimeUp,
                onTimeToVote = {
                    router.navigateToSingleDeviceVoting(accessCode)
                },
                onRestartGameClicked = {
                    viewModel.takeAction(SingleDeviceGamePlayViewModel.Action.ResetGame)
                },
                onEndGameClicked = {
                    viewModel.takeAction(SingleDeviceGamePlayViewModel.Action.EndGame)
                }
            )
        }

        // wrapping in navigation so they share the same view model
        navigation(
            route = singleDeviceVotingNavigationRoute.navRoute,
            startDestination = singleDeviceVotingRoute.navRoute,
        ) {

            composable(
                route = singleDeviceVotingRoute.navRoute,
                arguments = singleDeviceVotingRoute.navArguments
            ) {

                val viewModel: SingleDeviceVotingViewModel = it.viewModelScopedTo(
                    route = singleDeviceVotingNavigationRoute,
                    router = router
                )

                val state by viewModel.state.collectAsStateWithLifecycle()

                val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable

                LaunchedEffect(Unit) {
                    router.navigateToVotingInfo()
                    viewModel.takeAction(SingleDeviceVotingViewModel.Action.LoadGame)
                }

                LaunchedEffect(state.result) {
                    if (state.result != null) {
                        router.navigateToSingleDeviceVotingResults(accessCode)
                    }
                }

                ObserveWithLifecycle(flow = viewModel.events) { event ->
                    when (event) {
                        SingleDeviceVotingViewModel.Event.GameKilled -> router.goBack()
                        SingleDeviceVotingViewModel.Event.GameReset -> router.goBack()
                    }
                }

                SingleDeviceVotingScreen(
                    currentPlayer = state.currentPlayer,
                    isLastPlayer = state.isLastPlayer,
                    locationOptions = state.locationOptions,
                    playerOptions = state.playerOptions,
                    isResultsLoading = state.isLoadingResult,
                    onSeeResultsClicked = {
                        viewModel.takeAction(SingleDeviceVotingViewModel.Action.WaitForResults)
                    },
                    onSubmitPlayerVoteClicked = { currentPlayerId, voteId ->
                        viewModel.takeAction(
                            SubmitVoteForPlayer(
                                voterId = currentPlayerId,
                                playerId = voteId
                            )
                        )
                    },
                    onSubmitLocationVoteClicked = { currentPlayerId, location ->
                        viewModel.takeAction(
                            SingleDeviceVotingViewModel.Action.SubmitVoteForLocation(
                                voterId = currentPlayerId,
                                location = location
                            )
                        )
                    },
                )
            }

            composable(
                route = singleDeviceVotingResultsRoute.navRoute,
                arguments = singleDeviceVotingResultsRoute.navArguments
            ) {

                val viewModel: SingleDeviceVotingViewModel = it.viewModelScopedTo(
                    route = singleDeviceVotingNavigationRoute,
                    router = router
                )

                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.takeAction(SingleDeviceVotingViewModel.Action.LoadGame)
                }

                ObserveWithLifecycle(flow = viewModel.events) { event ->
                    when (event) {
                        SingleDeviceVotingViewModel.Event.GameKilled -> router.popBackTo(welcomeNavigationRoute)
                        SingleDeviceVotingViewModel.Event.GameReset -> {
                            router.popBackTo(singleDeviceVotingNavigationRoute, inclusive = true)
                            router.popBackTo(singleDeviceInfoRoute)
                        }
                    }
                }

                SingleDeviceResultsScreen(
                    onRestartClicked = {
                        viewModel.takeAction(SingleDeviceVotingViewModel.Action.ResetGame)
                    },
                    onEndGameClicked = {
                        viewModel.takeAction(SingleDeviceVotingViewModel.Action.EndGame)
                    },
                    didOddOneOutWin = state.result == GameResult.OddOneOutWon,
                    isTie = state.result == GameResult.Draw,
                    oddOneOutName = state.oddOneOutName,
                    locationName = state.location,
                    onVotingInfoClicked = { router.navigateToVotingInfo(hasVoted = true) },
                )
            }
        }
    }
}
