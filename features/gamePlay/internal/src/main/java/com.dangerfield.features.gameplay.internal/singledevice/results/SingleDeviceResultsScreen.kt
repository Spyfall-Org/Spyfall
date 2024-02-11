package com.dangerfield.features.gameplay.internal.singledevice.results

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.ads.ui.AdBanner
import com.dangerfield.features.gameplay.internal.singledevice.EndGameDialog
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.icon.CircularIcon
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.PreviewContent
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.gameplay.internal.R

@Composable
fun SingleDeviceResultsScreen(
    didOddOneOutWin: Boolean,
    modifier: Modifier = Modifier,
    isTie: Boolean,
    oddOneOutName: String,
    locationName: String,
    totalPlayerCount: Int,
    correctOddOneOutVoteCount: Int,
    oddOneOutLocationGuess: String?,
    onRestartClicked: () -> Unit,
    onEndGameClicked: () -> Unit,
    onVotingInfoClicked: () -> Unit
) {

    var showBackDialog by remember { mutableStateOf(false) }

    BackHandler {
        showBackDialog = !showBackDialog
    }

    Box {
        ResultsScreenContent(
            modifier,
            onVotingInfoClicked,
            isTie,
            didOddOneOutWin,
            oddOneOutName,
            correctOddOneOutVoteCount,
            totalPlayerCount,
            locationName,
            oddOneOutLocationGuess,
            onRestartClicked,
            onEndGameClicked
        )

        if (showBackDialog) {
            EndGameDialog(
                onDismissRequest = { showBackDialog = false },
                onEndGame = onEndGameClicked
            )
        }
    }
}

@Composable
private fun ResultsScreenContent(
    modifier: Modifier,
    onVotingInfoClicked: () -> Unit,
    isTie: Boolean,
    didOddOneOutWin: Boolean,
    oddOneOutName: String,
    correctOddOneOutVoteCount: Int,
    totalPlayerCount: Int,
    locationName: String,
    oddOneOutLocationGuess: String?,
    onRestartClicked: () -> Unit,
    onEndGameClicked: () -> Unit
) {
    Screen(
        modifier = modifier,
        topBar = {
            Column {
                AdBanner(ad = OddOneOutAd.SingleDeviceResults)
                ActionButtons(onVotingInfoClicked = onVotingInfoClicked)
            }
        }
    ) { padding ->

        ScrollingColumnWithFadingEdge(
            Modifier
                .padding(padding)
                .padding(horizontal = Spacing.S1000),
            horizontalAlignment = CenterHorizontally
        ) {

            VerticalSpacerS500()

            val winnerText = when {
                isTie -> dictionaryString(R.string.gameResults_tieResult_text)
                didOddOneOutWin -> dictionaryString(R.string.gameResults)
                else -> dictionaryString(R.string.gameResults_playersWonResult_text)
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = winnerText,
                textAlign = TextAlign.Center,
                typographyToken = OddOneOutTheme.typography.Display.D1200
            )

            VerticalSpacerS1200()

            Text(text = dictionaryString(R.string.singleDeviceGameResults_oddOneOut_header))

            VerticalSpacerS800()

            TextCard(oddOneOutName)

            VerticalSpacerS500()

            Text(
                text = dictionaryString(
                    R.string.singleDeviceGameResults_oddOneOutVotes_text,
                    mapOf(
                        "correct" to correctOddOneOutVoteCount.toString(),
                        "total" to totalPlayerCount.toString()
                    )
                ),
                typographyToken = OddOneOutTheme.typography.Body.B800
            )

            VerticalSpacerS1200()

            Text(text = dictionaryString(R.string.singleDeviceGameResults_location_header))

            VerticalSpacerS800()

            TextCard(locationName)

            if (oddOneOutLocationGuess != null) {
                VerticalSpacerS500()

                Text(
                    text = dictionaryString(
                        R.string.singeDeviceGameResults_locationGuessed_header,
                        mapOf("location" to oddOneOutLocationGuess)
                    ),
                    typographyToken = OddOneOutTheme.typography.Body.B800
                )
            }
            VerticalSpacerS1200()

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRestartClicked
            ) {
                Text(text = dictionaryString(R.string.singeDeviceGame_restart_action))
            }

            Spacer(modifier = Modifier.height(Spacing.S800))

            Button(
                type = ButtonType.Secondary,
                modifier = Modifier.fillMaxWidth(),
                onClick = onEndGameClicked
            ) {
                Text(text = dictionaryString(R.string.singeDeviceGameResults_endGame_action))
            }

            VerticalSpacerS1200()
        }
    }
}

@Composable
private fun ActionButtons(onVotingInfoClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.S500, top = Spacing.S500, end = Spacing.S500),
        horizontalArrangement = Arrangement.End
    ) {
        CircularIcon(
            icon = SpyfallIcon.Question(null),
            onClick = onVotingInfoClicked,
            padding = Spacing.S200,
            elevation = Elevation.Fixed,
            iconSize = IconSize.Medium,
        )
    }
}

@Composable
private fun TextCard(oddOneOutName: String) {
    Surface(
        color = OddOneOutTheme.colorScheme.surfacePrimary,
        contentColor = OddOneOutTheme.colorScheme.onSurfacePrimary,
        elevation = Elevation.Fixed,
        radius = Radii.Card,
        contentPadding = PaddingValues(Spacing.S1000)
    ) {
        Text(text = oddOneOutName)
    }
}

@Composable
@Preview
fun SingleDeviceResultsScreenPreview() {
    PreviewContent {
        SingleDeviceResultsScreen(
            onRestartClicked = { -> },
            onEndGameClicked = { -> },
            didOddOneOutWin = true,
            isTie = true,
            oddOneOutName = "Ryan",
            locationName = "A Church",
            onVotingInfoClicked = { -> },
            correctOddOneOutVoteCount = 3,
            oddOneOutLocationGuess = "A Jail",
            totalPlayerCount = 8
        )
    }
}