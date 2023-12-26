package com.dangerfield.features.gameplay.internal.singledevice.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.ThemePreviews
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
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun SingleDeviceResultsScreen(
    didOddOneOutWin: Boolean,
    isTie: Boolean,
    oddOneOutName: String,
    locationName: String,
    onRestartClicked: () -> Unit,
    onEndGameClicked: () -> Unit,
    onVotingInfoClicked: () -> Unit
) {
    Screen { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(vertical = Spacing.S1000)
                .padding(horizontal = Spacing.S1000),
            horizontalAlignment = CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CircularIcon(
                    icon = SpyfallIcon.Question(null),
                    iconSize = IconSize.Small,
                    onClick = onVotingInfoClicked,
                    padding = Spacing.S200,
                    elevation = Elevation.Fixed,
                )
            }
            VerticalSpacerS500()
            @Suppress("MaxLineLength")
            val winnerText = when {
                isTie -> "It's a tie!"
                didOddOneOutWin -> "The Odd One Out Won!"
                else -> "The Players Won!"
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text =  winnerText,
                textAlign = TextAlign.Center,
                typographyToken = OddOneOutTheme.typography.Display.D1100
            )

            VerticalSpacerS1200()

            Text(text = "The Odd One Out:")
            
            VerticalSpacerS800()

            TextCard(oddOneOutName)

            VerticalSpacerS1200()

            Text(text = "The Secret Location:")

            VerticalSpacerS800()

            TextCard(locationName)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRestartClicked
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
@ThemePreviews
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
        )
    }
}