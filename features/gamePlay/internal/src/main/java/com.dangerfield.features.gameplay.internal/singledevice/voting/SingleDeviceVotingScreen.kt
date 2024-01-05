package com.dangerfield.features.gameplay.internal.singledevice.voting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.features.ads.ui.AdBanner
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.Fake
import com.dangerfield.features.gameplay.internal.singledevice.EndGameDialog
import com.dangerfield.features.gameplay.internal.singledevice.EndOrGoBackDialog
import com.dangerfield.features.gameplay.internal.ui.GamePlayGrid
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SingleDeviceVotingScreen(
    currentPlayer: DisplayablePlayer?,
    isLastPlayer: Boolean,
    locationOptions: List<String>,
    playerOptions: List<DisplayablePlayer>,
    isResultsLoading: Boolean,
    isFirstPlayer: Boolean,
    onSubmitPlayerVoteClicked: (currentPlayerId: String, id: String) -> Unit,
    onSubmitLocationVoteClicked: (currentPlayerId: String, location: String) -> Unit,
    onSeeResultsClicked: () -> Unit,
    onPreviousPlayerClicked: () -> Unit,
    onEndGameClicked: () -> Unit,
) {
    var shouldShowExitDialog by remember { mutableStateOf(false) }
    var isVotingOptionsHidden by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    BackHandler {
        shouldShowExitDialog = !shouldShowExitDialog
    }

    Box {
        VotingScreenContent(
            scrollState = scrollState,
            currentPlayer = currentPlayer,
            locationOptions = locationOptions,
            playerOptions = playerOptions,
            isResultsLoading = isResultsLoading,
            onSubmitPlayerVoteClicked = onSubmitPlayerVoteClicked,
            onSubmitLocationVoteClicked = onSubmitLocationVoteClicked,
            isLastPlayer = isLastPlayer,
            onSeeResultsClicked = onSeeResultsClicked,
            isVotingOptionsHidden = isVotingOptionsHidden,
            onIsVotingOptionsHiddenChanged = { isVotingOptionsHidden = it },
        )

        if (shouldShowExitDialog) {
            when {
                !isFirstPlayer -> {
                    EndOrGoBackDialog(
                        onDismissRequest = { shouldShowExitDialog = false },
                        onEndGame = onEndGameClicked,
                        onGoBack = {
                            shouldShowExitDialog = false
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                                isVotingOptionsHidden = true
                                // wait for role to hide
                                delay(100)
                                onPreviousPlayerClicked()
                            }
                        }
                    )
                }

                else -> {
                    EndGameDialog(
                        onDismissRequest = { shouldShowExitDialog = false },
                        onEndGame = {
                            onEndGameClicked()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VotingScreenContent(
    scrollState: ScrollState,
    currentPlayer: DisplayablePlayer?,
    locationOptions: List<String>,
    playerOptions: List<DisplayablePlayer>,
    isResultsLoading: Boolean,
    isVotingOptionsHidden: Boolean,
    onIsVotingOptionsHiddenChanged: (Boolean) -> Unit,
    onSubmitPlayerVoteClicked: (currentPlayerId: String, id: String) -> Unit,
    onSubmitLocationVoteClicked: (currentPlayerId: String, location: String) -> Unit,
    isLastPlayer: Boolean,
    onSeeResultsClicked: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    var selectedPlayerForVote by rememberSaveable { mutableStateOf<DisplayablePlayer?>(null) }
    var selectedLocationForVote by rememberSaveable { mutableStateOf<String?>(null) }
    val hasSelectedVote: Boolean = selectedPlayerForVote != null || selectedLocationForVote != null

    Screen(
        topBar = {
            AdBanner(ad = OddOneOutAd.SingleDeviceVoting)
        }
    ) { padding ->

        ScrollingColumnWithFadingEdge(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.S1100)
                .padding(bottom = Spacing.S1000),
            horizontalAlignment = CenterHorizontally
        ) {

            // TODO add horizontal pager
            VerticalSpacerS1200()

            if (currentPlayer != null) {

                Text(
                    text = "Hand the device to ${currentPlayer.name}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typographyToken = OddOneOutTheme.typography.Display.D1100
                )

                VerticalSpacerS800()

                Text(
                    text = "Click \"show\" to see your voting options. The last player will be able to reveal the results.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typographyToken = OddOneOutTheme.typography.Body.B700
                )

                VerticalSpacerS1200()

                Button(
                    onClick = {
                        onIsVotingOptionsHiddenChanged(!isVotingOptionsHidden)
                    },
                    style = ButtonStyle.Filled,
                    size = ButtonSize.Small
                ) {
                    Text(text = if (isVotingOptionsHidden) "Show" else "Hide")
                }

                AnimatedVisibility(visible = !isVotingOptionsHidden) {

                    Column {

                        VerticalSpacerS1200()

                        if (currentPlayer.isOddOneOut) {
                            Text(text = "Which location were you at?")
                        } else {
                            Text(text = "Who is the odd one out?")
                        }

                        VerticalSpacerS500()

                        if (currentPlayer.isOddOneOut) {
                            GamePlayGrid(
                                items = locationOptions,
                                selectedItem = selectedLocationForVote,
                                isDisplayingForSelection = true,
                                isClickEnabled = true,
                                onItemSelectedForVote = { index ->
                                    selectedLocationForVote = index?.let { locationOptions[index] }
                                }
                            )
                        } else {
                            GamePlayGrid(
                                items = playerOptions.map { it.name },
                                isDisplayingForSelection = true,
                                selectedItem = selectedPlayerForVote?.name,
                                isClickEnabled = true,
                                onItemSelectedForVote = { index ->
                                    selectedPlayerForVote = index?.let { playerOptions[index] }
                                }
                            )
                        }
                    }
                }

                VerticalSpacerS1200()

                if (isResultsLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Button(
                        onClick = {
                            // TODO this view is super super stateful
                            onIsVotingOptionsHiddenChanged(true)
                            coroutineScope.launch {
                                delay(200)
                                scrollState.animateScrollTo(0)
                                val playerVote = selectedPlayerForVote
                                val locationVote = selectedLocationForVote
                                if (playerVote != null) {
                                    onSubmitPlayerVoteClicked(currentPlayer.id, playerVote.id)
                                } else if (locationVote != null) {
                                    onSubmitLocationVoteClicked(currentPlayer.id, locationVote)
                                }
                                if (isLastPlayer) {
                                    onSeeResultsClicked()
                                }
                                selectedPlayerForVote = null
                                selectedLocationForVote = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        style = if (hasSelectedVote) ButtonStyle.Filled else ButtonStyle.Outlined,
                        enabled = hasSelectedVote,
                    ) {
                        Text(text = if (isLastPlayer) "See Results" else "Next Player")
                    }
                }

                VerticalSpacerS1200()

            } else {
                Text(
                    text = "Loading...",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typographyToken = OddOneOutTheme.typography.Display.D1100,
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewSingleDeviceVotingScreen() {
    PreviewContent {
        SingleDeviceVotingScreen(
            currentPlayer = DisplayablePlayer(
                name = "Player 1",
                id = "",
                role = "The Odd One Out",
                isFirst = false,
                isOddOneOut = true
            ),
            isLastPlayer = false,
            locationOptions = listOf("Bank", "School", "Hospital", "Park", "Mall", "Restaurant"),
            playerOptions = Fake().players,
            onSeeResultsClicked = { -> },
            onSubmitPlayerVoteClicked = { _, _ -> },
            onSubmitLocationVoteClicked = { _, _ -> },
            isResultsLoading = false,
            isFirstPlayer = false,
            onPreviousPlayerClicked = { -> },
            onEndGameClicked = { -> },
        )
    }
}