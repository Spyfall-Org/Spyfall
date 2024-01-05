package com.dangerfield.features.gameplay.internal.singledevice.gameplay

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.features.ads.ui.AdBanner
import com.dangerfield.features.ads.OddOneOutAd
import com.dangerfield.features.gameplay.internal.singledevice.EndGameDialog
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

// TODO consider making examples backend driven or rotating
@Composable
fun SingleDeviceGamePlayScreen(
    timeRemaining: String,
    isTimeUp: Boolean,
    modifier: Modifier = Modifier,
    onTimeToVote: () -> Unit = {},
    onRestartGameClicked: () -> Unit = {},
    onEndGameClicked: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    var shouldShowExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        shouldShowExitDialog = !shouldShowExitDialog
    }

    Box {
        GamePlayScreenContent(
            modifier,
            isTimeUp,
            scrollState,
            onTimeToVote,
            timeRemaining,
            onRestartGameClicked,
            onEndGameClicked
        )

        if(shouldShowExitDialog) {
            EndGameDialog(
                onDismissRequest = { shouldShowExitDialog = false },
                onEndGame = onEndGameClicked
            )
        }
    }
}

@Composable
private fun GamePlayScreenContent(
    modifier: Modifier,
    isTimeUp: Boolean,
    scrollState: ScrollState,
    onTimeToVote: () -> Unit,
    timeRemaining: String,
    onRestartGameClicked: () -> Unit,
    onEndGameClicked: () -> Unit
) {

    LaunchedEffect(isTimeUp) {
        if (isTimeUp) {
            scrollState.animateScrollTo(0)
            onTimeToVote()
        }
    }

    Screen(
        modifier = modifier,
        topBar = {
            AdBanner(ad = OddOneOutAd.SingleDeviceGamePlayBanner)
        }
    ) {
            ScrollingColumnWithFadingEdge(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = Spacing.S1000)
                ,
                state = scrollState,
            ) {

                VerticalSpacerS1200()

                Text(
                    text = timeRemaining,
                    typographyToken = OddOneOutTheme.typography.Display.D1400,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                VerticalSpacerS800()

                Text(
                    text = "Take turns asking each other questions that only someone at the secret location would know.\n\nBut make sure not to reveal the location to the odd one out!",
                    typographyToken = OddOneOutTheme.typography.Body.B800,
                )

                VerticalSpacerS800()

                Text(
                    text = "Example:",
                    typographyToken = OddOneOutTheme.typography.Body.B800.Bold,
                )

                VerticalSpacerS800()

                BulletRow {
                    Text(
                        text = "Do you come to this location often?",
                        typographyToken = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerS800()

                BulletRow {
                    Text(
                        text = "Can you make money at this location?",
                        typographyToken = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerS800()

                BulletRow {
                    Text(
                        text = "Do you have to wear a uniform?",
                        typographyToken = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerS800()

                BulletRow {
                    Text(
                        text = "Could you take kids to this location?",
                        typographyToken = OddOneOutTheme.typography.Body.B800.Italic,
                    )
                }

                VerticalSpacerS1200()

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRestartGameClicked
                ) {
                    Text(text = "Restart Game")
                }

                Spacer(modifier = Modifier.height(Spacing.S800))

                Button(
                    type = ButtonType.Regular,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEndGameClicked
                ) {
                    Text(text = "End Game")
                }

                VerticalSpacerS1200()

            }
    }
}

@Composable
@ThemePreviews
private fun SingleDeviceGamePlayScreenPreview() {
    PreviewContent {
        SingleDeviceGamePlayScreen(
            timeRemaining = "3:12",
            isTimeUp = false,
        )
    }
}