package com.dangerfield.features.gameplay.internal.singledevice

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dangerfield.features.ads.AdsConfig
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.blockingerror.navigateToGeneralErrorDialog
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.millisToMMss
import com.dangerfield.features.gameplay.internal.navigateToSingleDevicePlayerRoleRoute
import com.dangerfield.features.gameplay.internal.playDingSound
import com.dangerfield.features.gameplay.internal.singleDevicePlayerRoleRoute
import com.dangerfield.features.gameplay.internal.singleDeviceVotingParentRoute
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayScreen
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Event.GameKilled
import com.dangerfield.features.gameplay.internal.singledevice.gameplay.SingleDeviceGamePlayViewModel.Event.GameReset
import com.dangerfield.features.gameplay.internal.singledevice.info.SingleDeviceInfoScreen
import com.dangerfield.features.gameplay.internal.singledevice.info.SingleDeviceInfoViewModel
import com.dangerfield.features.gameplay.internal.singledevice.results.SingleDeviceResultsScreen
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDevicePlayerRoleScreen
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.EndGame
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.LoadGame
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.LoadNextPlayer
import com.dangerfield.features.gameplay.internal.singledevice.rolereveal.SingleDeviceRoleRevealViewModel.Action.LoadPreviousPlayer
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
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.coreflowroutines.ObserveWithLifecycle
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.navigation.FeatureNavBuilder
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.navigation.viewModelScopedTo
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class SingleDeviceGamePlayFeatureNavGraphBuilder @Inject constructor(
    private val interstitialAd: com.dangerfield.features.ads.ui.InterstitialAd<OddOneOutAd.GameRestartInterstitial>,
    private val adsConfig: AdsConfig
) : FeatureNavBuilder {

    private var numberOfRestarts = 0

    @Suppress("LongMethod")
    override fun NavGraphBuilder.buildNavGraph(router: Router) {

        composable(
            route = singleDeviceInfoRoute.navRoute,
            arguments = singleDeviceInfoRoute.navArguments
        ) {
            val viewModel = hiltViewModel<SingleDeviceInfoViewModel>()
            val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable
            val timeLimit = it.navArgument<Int>(timeLimitArgument) ?: return@composable
            val context = LocalContext.current

            PageLogEffect(
                route = singleDeviceInfoRoute,
                type = PageType.FullScreenPage
            )

            LaunchedEffect(Unit) {
                val restartFrequency = adsConfig.gameRestInterstitialAdFrequency
                if (numberOfRestarts > 0 && numberOfRestarts % restartFrequency == 0) {
                    interstitialAd.show(context, onAdDismissed = {})
                }
            }

            ObserveWithLifecycle(viewModel.eventFlow) { event ->
                when (event) {
                    SingleDeviceInfoViewModel.Event.GameEnded -> router.popBackTo(
                        welcomeNavigationRoute
                    )
                }
            }

            SingleDeviceInfoScreen(
                onStartClicked = {
                    router.navigateToSingleDevicePlayerRoleRoute(
                        accessCode = accessCode,
                        timeLimit = timeLimit
                    )
                },
                onEndGameClicked = {
                    viewModel.takeAction(SingleDeviceInfoViewModel.Action.EndGame)
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
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()

            PageLogEffect(
                route = singleDevicePlayerRoleRoute,
                type = PageType.FullScreenPage
            )

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

            ObserveWithLifecycle(viewModel.eventFlow) { event ->
                when (event) {
                    SingleDeviceRoleRevealViewModel.Event.GameKilled -> router.popBackTo(
                        welcomeNavigationRoute
                    )
                }
            }

            SingleDevicePlayerRoleScreen(
                currentPlayer = state.currentPlayer,
                gameItem = state.location,
                onNextPlayerClicked = { viewModel.takeAction(LoadNextPlayer) },
                isLastPlayer = state.isLastPlayer,
                onStartGameClicked = { viewModel.takeAction(StartGame) },
                nameFieldState = state.nameFieldState,
                onNameUpdated = { viewModel.takeAction(UpdateName(it)) },
                locationOptions = state.locationOptions,
                onEndGameClicked = { viewModel.takeAction(EndGame) },
                onPreviousPlayerClicked = { viewModel.takeAction(LoadPreviousPlayer) },
                isFirstPlayer = state.isFirstPlayer
            )
        }

        composable(
            route = singleDeviceGamePlayRoute.navRoute,
            arguments = singleDeviceGamePlayRoute.navArguments
        ) {
            val viewModel = hiltViewModel<SingleDeviceGamePlayViewModel>()
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()
            val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable
            val context = LocalContext.current

            PageLogEffect(
                route = singleDeviceGamePlayRoute,
                type = PageType.FullScreenPage
            )

            ObserveWithLifecycle(flow = viewModel.eventFlow) { event ->
                when (event) {
                    is GameReset -> router.popBackTo(singleDeviceInfoRoute).also {
                        numberOfRestarts++
                    }

                    GameKilled -> router.popBackTo(welcomeNavigationRoute)
                    SingleDeviceGamePlayViewModel.Event.ResetFailed -> router.navigateToGeneralErrorDialog()
                }
            }

            LaunchedEffect(Unit) {
                viewModel.takeAction(SingleDeviceGamePlayViewModel.Action.LoadGame)
            }

            SingleDeviceGamePlayScreen(
                timeRemaining = state.timeRemainingMillis.millisToMMss(),
                isTimeUp = state.isTimeUp,
                onTimeToVote = {
                    playDingSound(context)
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
            route = singleDeviceVotingParentRoute.navRoute,
            startDestination = singleDeviceVotingRoute.navRoute,
        ) {

            composable(
                route = singleDeviceVotingRoute.navRoute,
                arguments = singleDeviceVotingRoute.navArguments
            ) {

                val viewModel: SingleDeviceVotingViewModel = it.viewModelScopedTo(
                    route = singleDeviceVotingParentRoute,
                    router = router
                )

                val state by viewModel.stateFlow.collectAsStateWithLifecycle()

                val accessCode = it.navArgument<String>(accessCodeArgument) ?: return@composable

                PageLogEffect(
                    route = singleDeviceVotingRoute,
                    type = PageType.FullScreenPage
                )

                LaunchedEffect(Unit) {
                    router.navigateToVotingInfo()
                    viewModel.takeAction(SingleDeviceVotingViewModel.Action.LoadGame)
                }

                LaunchedEffect(state.result) {
                    if (state.result != null) {
                        router.navigateToSingleDeviceVotingResults(accessCode)
                    }
                }

                ObserveWithLifecycle(flow = viewModel.eventFlow) { event ->
                    when (event) {
                        SingleDeviceVotingViewModel.Event.GameKilled -> router.popBackTo(
                            welcomeNavigationRoute
                        )
                        SingleDeviceVotingViewModel.Event.GameReset -> router.goBack()
                        SingleDeviceVotingViewModel.Event.ResetFailed -> router.navigateToGeneralErrorDialog()
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
                    isFirstPlayer = state.isFirstPlayer,
                    onPreviousPlayerClicked = {
                        viewModel.takeAction(
                            SingleDeviceVotingViewModel.Action.PreviousPlayer
                        )
                    },
                    onEndGameClicked = {
                        viewModel.takeAction(
                            SingleDeviceVotingViewModel.Action.EndGame
                        )
                    },
                    onRestartGameClicked = {
                        viewModel.takeAction(
                            SingleDeviceVotingViewModel.Action.ResetGame
                        )
                    }
                )
            }

            composable(
                route = singleDeviceVotingResultsRoute.navRoute,
                arguments = singleDeviceVotingResultsRoute.navArguments
            ) {

                val viewModel: SingleDeviceVotingViewModel = it.viewModelScopedTo(
                    route = singleDeviceVotingParentRoute,
                    router = router
                )

                val state by viewModel.stateFlow.collectAsStateWithLifecycle()

                PageLogEffect(
                    route = singleDeviceVotingResultsRoute,
                    type = PageType.FullScreenPage
                )

                LaunchedEffect(Unit) {
                    viewModel.takeAction(SingleDeviceVotingViewModel.Action.LoadGame)
                }

                ObserveWithLifecycle(flow = viewModel.eventFlow) { event ->
                    when (event) {
                        SingleDeviceVotingViewModel.Event.GameKilled -> router.popBackTo(
                            welcomeNavigationRoute
                        )

                        SingleDeviceVotingViewModel.Event.GameReset -> {
                            router.popBackTo(singleDeviceInfoRoute).also {
                                numberOfRestarts++
                            }
                        }
                        SingleDeviceVotingViewModel.Event.ResetFailed -> router.navigateToGeneralErrorDialog()
                    }
                }

                SingleDeviceResultsScreen(
                    onRestartClicked = {
                        viewModel.takeAction(SingleDeviceVotingViewModel.Action.ResetGame)
                    },
                    onEndGameClicked = {
                        viewModel.takeAction(SingleDeviceVotingViewModel.Action.EndGame)
                    },
                    totalPlayerCount = state.totalPlayerCount,
                    results = state.result,
                    oddOneOutName = state.oddOneOutName,
                    locationName = state.location,
                    onVotingInfoClicked = { router.navigateToVotingInfo(hasVoted = true) },
                    correctOddOneOutVoteCount = state.correctGuessesForOddOneOut,
                    oddOneOutLocationGuess = state.oddOneOutLocationGuess,
                )
            }
        }
    }
}
