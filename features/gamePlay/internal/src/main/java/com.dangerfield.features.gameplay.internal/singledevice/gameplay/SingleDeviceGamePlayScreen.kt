package com.dangerfield.features.gameplay.internal.singledevice.gameplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.S1000)
    ) {

        LaunchedEffect(isTimeUp) {
            if (isTimeUp) {
                scrollState.animateScrollTo(0)
                onTimeToVote()
            }
        }

        ScrollingColumnWithFadingEdge(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {

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
        }

        Column() {

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