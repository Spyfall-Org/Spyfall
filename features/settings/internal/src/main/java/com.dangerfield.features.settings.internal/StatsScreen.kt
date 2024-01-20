package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dangerfield.libraries.dictionary.dictionaryString
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
import com.dangerfield.oddoneoout.features.settings.internal.R

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
                title = dictionaryString(R.string.settings_stats_header),
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
                Icon(spyfallIcon = SpyfallIcon.Info(null))
                HorizontalSpacerS200()
                Text(
                    text = dictionaryString(R.string.stats_infoNote_label),
                    typographyToken = OddOneOutTheme.typography.Body.B700,
                    color = OddOneOutTheme.colorScheme.onSurfacePrimary
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            VerticalSpacerS500()

            Text(
                text = dictionaryString(R.string.stats_infoNote_body),
                typographyToken = OddOneOutTheme.typography.Body.B700,
                color = OddOneOutTheme.colorScheme.onSurfacePrimary
            )

            VerticalSpacerS1200()

            Text(
                text = dictionaryString(R.string.stats_mutliDeviceGames_header),
                typographyToken = OddOneOutTheme.typography.Display.D1000.Bold
            )

            VerticalSpacerS800()

            Card {
                Column(
                    modifier = Modifier.padding(Spacing.S1000)
                ) {

                    Text(text = dictionaryString(R.string.stats_totalGamesPlayed_header))

                    Text(
                        text = "$totalMultiDeviceGamesPlayed",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )

                    VerticalSpacerS800()

                    Text(text = dictionaryString(R.string.stats_gamesYouveWon_header))

                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveWonAsOddOneOut_label,
                            mapOf("gamesWon" to gamesWonAsOddOne.toString())
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveWonAsPlayer_label,
                            mapOf("gamesWon" to gamesWonAsPlayer.toString())
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )


                    VerticalSpacerS1200()

                    Text(text = dictionaryString(R.string.stats_gamesYouveLost_header))

                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveLostAsOddOneOut_label,
                            mapOf("gamesLost" to gamesLostAsOddOne.toString())
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveLostAsPlayer_label,
                            mapOf("gamesLost" to gamesLostAsPlayer.toString())
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                }
            }

            VerticalSpacerS1200()

            Text(
                text = dictionaryString(R.string.stats_singleDeviceGames_header),
                typographyToken = OddOneOutTheme.typography.Display.D1000.Bold
            )

            VerticalSpacerS800()

            Card {
                Column(
                    modifier = Modifier.padding(Spacing.S1000),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ){

                    Text(text = dictionaryString(R.string.stats_singleDeviceTotalGamesPlayed_header))

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
