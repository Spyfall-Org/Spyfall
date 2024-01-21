package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun PacksInfoBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    packs: List<Pack>,
    onDismiss: (BottomSheetState) -> Unit
) {

    PageLogEffect(
        route = route("packs_info_bottom_sheet"),
        type = PageType.BottomSheet
    )

    BasicBottomSheet(
        onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.newGame_packsInfo_header))
        },
        content = {
            ScrollingColumnWithFadingEdge {
                Text(
                    text = dictionaryString(R.string.packsInfo_packsDescription_text),
                )
                if (packs.isNotEmpty()) {
                    VerticalSpacerS800()
                    packs.forEach { pack ->
                        Text(text = pack.name, typographyToken = OddOneOutTheme.typography.Default)
                        VerticalSpacerS500()
                        NonLazyVerticalGrid(
                            columns = 2,
                            data = pack.locations
                        ) { _, item ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(text = dictionaryString(R.string.packsInfo_bulletPoint_text))
                                HorizontalSpacerS600()
                                Text(text = item.name)
                            }
                        }

                        VerticalSpacerS1200()
                    }

                    VerticalSpacerS800()
                }
            }
        },
        bottomContent = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.S800),
                onClick = { onDismiss(bottomSheetState) }
            ) {
                Text(text = dictionaryString(id = R.string.app_okay_action))
            }
        }
    )
}

@Composable
@ThemePreviews
private fun PreviewPackDetailsBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
    val exampleLocations = listOf(
        "School",
        "Hospital",
        "Mall",
        "Park",
        "Airport",
        "Cruise Ship",
        "Train Station",
        "Bank",
        "Casino",
        "Hotel",
        "Military Base",
        "Movie Studio",
        "Police Station",
        "Restaurant",
        "Service Station",
        "Space Station",
        "Submarine",
        "Supermarket",
        "Theater",
        "University",
        "Zoo"
    )
    PreviewContent {
        PacksInfoBottomSheet(
            onDismiss = {},
            bottomSheetState = bottomSheetState,
            packs = listOf(
                Pack(name = "Pack one", locations = exampleLocations.shuffled().take(7).map {
                    Location(
                        name = it,
                        roles = listOf(),
                        packName = "Pack one"
                    )
                }),

                Pack(name = "Pack two", locations = exampleLocations.shuffled().take(8).map {
                    Location(
                        name = it,
                        roles = listOf(),
                        packName = "Pack two"
                    )
                }),

                Pack(name = "Pack two", locations = exampleLocations.shuffled().take(5).map {
                    Location(
                        name = it,
                        roles = listOf(),
                        packName = "Pack two"
                    )
                }),
            ),
        )
    }
}
