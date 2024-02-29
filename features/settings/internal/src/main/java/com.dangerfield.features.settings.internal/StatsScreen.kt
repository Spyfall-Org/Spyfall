package com.dangerfield.features.settings.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.HorizontalSpacerD200
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
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
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Dimension.D1000)
                .verticalScroll(rememberScrollState())
            ,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            VerticalSpacerD1200()

            Row {
                Icon(spyfallIcon = SpyfallIcon.Info(null))
                HorizontalSpacerD200()
                Text(
                    text = dictionaryString(R.string.stats_infoNote_label),
                    typographyToken = OddOneOutTheme.typography.Body.B700,
                    colorResource = OddOneOutTheme.colors.onSurfacePrimary
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            VerticalSpacerD500()

            Text(
                text = dictionaryString(R.string.stats_infoNote_body),
                typographyToken = OddOneOutTheme.typography.Body.B700,
                colorResource = OddOneOutTheme.colors.onSurfacePrimary
            )

            VerticalSpacerD1200()

            Text(
                text = dictionaryString(R.string.stats_multiDeviceGames_header),
                typographyToken = OddOneOutTheme.typography.Display.D1000.Bold
            )

            VerticalSpacerD800()

            Card {
                Column(
                    modifier = Modifier.padding(Dimension.D1000)
                ) {

                    Text(text = dictionaryString(R.string.stats_totalGamesPlayed_header))

                    Text(
                        text = "$totalMultiDeviceGamesPlayed",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )

                    VerticalSpacerD800()

                    Text(text = dictionaryString(R.string.stats_gamesYouveWon_header))

                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveWonAsOddOneOut_label,
                            "gamesWon" to gamesWonAsOddOne.toString()
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveWonAsPlayer_label,
                            "gamesWon" to gamesWonAsPlayer.toString()
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )


                    VerticalSpacerD1200()

                    Text(text = dictionaryString(R.string.stats_gamesYouveLost_header))

                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveLostAsOddOneOut_label,
                            "gamesLost" to gamesLostAsOddOne.toString()
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                    Text(
                        text = dictionaryString(
                            R.string.stats_gamesYouveLostAsPlayer_label,
                            "gamesLost" to gamesLostAsPlayer.toString()
                        ),
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                }
            }

            VerticalSpacerD1200()

            Text(
                text = dictionaryString(R.string.stats_singleDeviceGames_header),
                typographyToken = OddOneOutTheme.typography.Display.D1000.Bold
            )

            VerticalSpacerD800()

            Card {
                Column(
                    modifier = Modifier.padding(Dimension.D1000),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ){

                    Text(text = dictionaryString(R.string.stats_singleDeviceTotalGamesPlayed_header))

                    Text(
                        text = "$totalSingleDeviceGamesPlayed",
                        typographyToken = OddOneOutTheme.typography.Body.B700
                    )
                }
            }

            VerticalSpacerD1200()

        }
    }
}


@Composable
private fun Card(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.bounceClick(scaleDown = 0.9f),
        color = OddOneOutTheme.colors.surfacePrimary,
        contentColor = OddOneOutTheme.colors.onSurfacePrimary,
        elevation = Elevation.Button,
        radius = Radii.Card,
        contentPadding = PaddingValues(Dimension.D1000)
    ) {
        content()
    }
}

@Composable
@Preview
private fun PreviewSettingsScreen() {
    Preview {
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
