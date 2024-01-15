package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.HorizontalSpacerS200
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    gamesLostAsPlayer: Int,
    gamesLostAsOddOne: Int,
    gamesWonAsPlayer: Int,
    gamesWonAsOddOne: Int,
    totalMultiDeviceGamesPlayed: Int,
    totalSingleDeviceGamesPlayed: Int,
    onNavigateBack: () -> Unit = { },
) {
    Screen(
        modifier = modifier,
        topBar = {
            Header(
                title = "Stats",
                onNavigateBack = onNavigateBack
            )
        }
    ) {
        ScrollingColumnWithFadingEdge(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Spacing.S1000)
            ,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            VerticalSpacerS1200()

            Row {
                Icon(spyfallIcon = SpyfallIcon.Info(""))
                HorizontalSpacerS200()
                Text(
                    text = "Note",
                    typographyToken = OddOneOutTheme.typography.Body.B700,
                    color = OddOneOutTheme.colorScheme.onSurfacePrimary
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            VerticalSpacerS500()

            Text(
                text = "Games results are not collected when players vote amongst themselves instead of in the app. Because of this the stats here may not fully represent wins and losses.",
                typographyToken = OddOneOutTheme.typography.Body.B700,
                color = OddOneOutTheme.colorScheme.onSurfacePrimary
            )

            VerticalSpacerS1200()

            Text(
                text = "Multi Device Games",
                typographyToken = OddOneOutTheme.typography.Display.D1000.Bold
            )

            VerticalSpacerS800()

            Card {
                Column(
                    modifier = Modifier.padding(Spacing.S1000)
                ) {

                    Text(text = "Total Games Played:")

                    Text(
                        text = "$totalMultiDeviceGamesPlayed",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )

                    VerticalSpacerS800()

                    Text(text = "Games You've Won:")

                    Text(
                        text = "As Odd One Out -  $gamesWonAsOddOne",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                    Text(
                        text = "As Player -  $gamesWonAsPlayer",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )


                    VerticalSpacerS1200()

                    Text(text = "Games You've Lost:")

                    Text(
                        text = "As Odd One Out -  $gamesLostAsOddOne",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                    Text(
                        text = "As Player -  $gamesLostAsPlayer",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                }
            }

            VerticalSpacerS1200()

            Text(
                text = "Single Device Games",
                typographyToken = OddOneOutTheme.typography.Display.D1000.Bold
            )

            VerticalSpacerS800()

            Card {
                Column(
                    modifier = Modifier.padding(Spacing.S1000),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ){

                    Text(text = "Total Games Played:")

                    Text(
                        text = "$totalSingleDeviceGamesPlayed",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                }
            }

            VerticalSpacerS1200()

        }
    }
}


@Composable
private fun Card(
    content: @Composable () -> Unit
) {
    Surface(
        color = OddOneOutTheme.colorScheme.surfacePrimary,
        contentColor = OddOneOutTheme.colorScheme.onSurfacePrimary,
        elevation = Elevation.Fixed,
        radius = Radii.Card,
        contentPadding = PaddingValues(Spacing.S1000)
    ) {
        content()
    }
}

@Composable
@ThemePreviews
private fun PreviewSettingsScreen() {
    PreviewContent {
        StatsScreen(
            onNavigateBack = { -> },
            gamesLostAsPlayer = 4649,
            gamesLostAsOddOne = 3420,
            gamesWonAsPlayer = 7348,
            gamesWonAsOddOne = 8085,
            totalSingleDeviceGamesPlayed = 34,
            totalMultiDeviceGamesPlayed = 43
        )
    }
}
