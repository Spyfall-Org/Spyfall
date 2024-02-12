package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun PacksInfoBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    packs: List<DisplayablePack>,
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
                        val numberText =
                            pack.number.toIntOrNull()?.let { if (it > 1) " ($it)" else "" } ?: ""

                        Text(
                            text = pack.type + numberText,
                            typographyToken = OddOneOutTheme.typography.Default
                        )
                        VerticalSpacerS500()
                        NonLazyVerticalGrid(
                            columns = 2,
                            data = pack.pack.locations
                        ) { _, item ->
                            BulletRow(modifier = Modifier.fillMaxWidth()) {
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
                    .fillMaxWidth(),
                onClick = { onDismiss(bottomSheetState) }
            ) {
                Text(text = dictionaryString(id = R.string.app_okay_action))
            }
        }
    )
}

@Composable
@Preview
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
                DisplayablePack(
                    pack = Pack(
                        name = "Pack one",
                        locations = exampleLocations.shuffled().take(7).map {
                            Location(
                                name = it,
                                roles = listOf(),
                                packName = "Pack one"
                            )
                        }
                    )
                ),
                DisplayablePack(
                    pack = Pack(
                        name = "Pack two",
                        locations = exampleLocations.shuffled().take(7).map {
                            Location(
                                name = it,
                                roles = listOf(),
                                packName = "Pack two"
                            )
                        }
                    )
                ),

                ),
        )
    }
}
