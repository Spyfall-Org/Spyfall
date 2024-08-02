package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.newgame.internal.presentation.model.NewGamePackOption
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.BulletRow
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun PacksInfoBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    packs: List<NewGamePackOption>,
    onDismiss: (BottomSheetState) -> Unit
) {
    PageLogEffect(
        route = route("packs_info_bottom_sheet"), type = PageType.BottomSheet
    )

    BasicBottomSheet(onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        modifier = modifier,
        stickyTopContent = {
            Text(text = dictionaryString(R.string.newGame_packsInfo_header))
        },
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = dictionaryString(R.string.packsInfo_packsDescription_text),
                )
                if (packs.isNotEmpty()) {
                    VerticalSpacerD800()
                    packs.forEach { pack ->
                        val numberText =
                            pack.number?.toIntOrNull()?.let { if (it > 1) " ($it)" else "" } ?: ""

                        Text(
                            text = pack.type + numberText,
                            typography = OddOneOutTheme.typography.Default
                        )
                        VerticalSpacerD500()
                        NonLazyVerticalGrid(
                            columns = 2, data = pack.pack?.items.orEmpty()
                        ) { _, item ->
                            BulletRow(modifier = Modifier.fillMaxWidth()) {
                                Text(text = item.name)
                            }
                        }

                        VerticalSpacerD1200()
                    }

                    VerticalSpacerD800()
                }
            }
        },
        stickyBottomContent = {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onDismiss(bottomSheetState) }) {
                Text(text = dictionaryString(id = R.string.app_okay_action))
            }
        })
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
    ).map { PackItem.Location(it, roles = emptyList()) }
    Preview {
        PacksInfoBottomSheet(
            onDismiss = {},
            bottomSheetState = bottomSheetState,
            packs = listOf(
                NewGamePackOption(
                    isSelected = false, pack = Pack.LocationPack(
                        locations = exampleLocations,
                        name = "Super Special Pack 4",
                        id = "4",
                        version = 1,
                        languageCode = "en",
                        isPublic = false,
                        owner = OwnerDetails.App,
                        isUserSaved = false
                    )
                ),
                NewGamePackOption(
                    isSelected = false, pack = Pack.LocationPack(
                        locations = exampleLocations,
                        name = "Super Special Pack 4",
                        id = "4",
                        version = 1,
                        languageCode = "en",
                        isPublic = false,
                        owner = OwnerDetails.App,
                        isUserSaved = false
                    )
                ),
            ),
        )
    }
}
