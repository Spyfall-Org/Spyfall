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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1000
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.gameplay.internal.R
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
    onRestartGameClicked: () -> Unit
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
            onRestartGameClicked = onRestartGameClicked,
            onEndGameClicked = onEndGameClicked
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
    onRestartGameClicked: () -> Unit,
    onEndGameClicked: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    var selectedPlayerForVote by remember { mutableStateOf<DisplayablePlayer?>(null) }
    var selectedLocationForVote by remember { mutableStateOf<String?>(null) }
    val hasSelectedVote: Boolean = selectedPlayerForVote != null || selectedLocationForVote != null

    Screen(
        topBar = {
            AdBanner(ad = OddOneOutAd.SingleDeviceVoting)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Dimension.D1100)
                .padding(bottom = Dimension.D1000)
                .verticalScroll(scrollState)
            ,
            horizontalAlignment = CenterHorizontally
        ) {

            VerticalSpacerD1200()

            if (currentPlayer != null) {

                Text(
                    text = dictionaryString(
                        id = R.string.singleDevice_handToPlayer_header,
                        "name" to currentPlayer.name
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typography = OddOneOutTheme.typography.Display.D1100
                )

                VerticalSpacerD800()

                Text(
                    text = dictionaryString(R.string.singleDeviceVoting_Instructions_text),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typography = OddOneOutTheme.typography.Body.B700
                )

                VerticalSpacerD1200()

                Button(
                    onClick = {
                        onIsVotingOptionsHiddenChanged(!isVotingOptionsHidden)
                    },
                    style = ButtonStyle.Background,
                    size = ButtonSize.Small
                ) {
                    Text(
                        text = if (isVotingOptionsHidden) {
                            dictionaryString(R.string.app_show_action)
                        } else {
                            dictionaryString(R.string.app_hide_action)
                        }
                    )
                }

                AnimatedVisibility(visible = !isVotingOptionsHidden) {

                    Column {

                        VerticalSpacerD1200()

                        if (currentPlayer.isOddOneOut) {
                            Text(text = dictionaryString(R.string.singleDeviceVoting_locationVoting_header))
                        } else {
                            Text(text = dictionaryString(R.string.singleDeviceVoting_oddOneOutVoting_header))
                        }

                        VerticalSpacerD500()

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

                VerticalSpacerD1200()

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
                            onIsVotingOptionsHiddenChanged(true)
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                                delay(100)
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
                        style = ButtonStyle.Background ,
                        enabled = hasSelectedVote,
                    ) {
                        Text(
                            text = if (isLastPlayer) {
                                dictionaryString(R.string.singleDeviceVoting_seeResults_action)
                            } else {
                                dictionaryString(R.string.singleDeviceVoting_nextPlayer_action)
                            }
                        )
                    }
                }

                VerticalSpacerD1200()

            } else {
                Text(
                    text = dictionaryString(R.string.singleDeviceVoting_loading_text),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    typography = OddOneOutTheme.typography.Display.D1100,
                )
            }

            VerticalSpacerD800()

            Text(text = dictionaryString(R.string.app_or_text))

            VerticalSpacerD800()

            Button(
                onClick = onRestartGameClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(R.string.singeDeviceGame_restart_action))
            }

            VerticalSpacerD1000()

            Button(
                type = ButtonType.Secondary,
                onClick = onEndGameClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(R.string.singleDeviceInfo_endGame_action))
            }

            VerticalSpacerD1000()
        }
    }
}

@Composable
@Preview
private fun PreviewSingleDeviceVotingScreen() {
    Preview {
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
            onRestartGameClicked = {}
        )
    }
}