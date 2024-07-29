package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun GamePackGrid(
    gamePacks: List<DisplayablePack>,
    onPackSelected: (DisplayablePack, Boolean) -> Unit
) {
    val packs = gamePacks

    NonLazyVerticalGrid(
        columns = 3,
        data = packs,
        verticalSpacing = Dimension.D300,
        horizontalSpacing = Dimension.D300,
        itemContent = { index, gamePack ->
            GamePackItem(
                modifier = Modifier
                    .fillMaxHeight()
                    .heightIn(min = 100.dp),
                colorResource = ThemeColor.entries.toTypedArray()
                    .let { it[index % it.size] }.colorResource,
                packName = gamePack.type,
                isSelected = gamePack.isSelected,
                isEnabled = gamePack.isEnabled,
                number = gamePack.number?.toIntOrNull(),
                onClick = {
                    onPackSelected(gamePack, it)
                }
            )
        }
    )
}

@Composable
@Preview
private fun PreviewGamePackGrid() {
    Preview {
        var packs by remember {
            mutableStateOf(
                listOf(
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),

                    DisplayablePack(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),

                    DisplayablePack(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack.LocationPack(
                            locations = listOf(),
                            name = "Super Special Pack 4",
                            id = "4",
                            version = 1,
                            languageCode = "en",
                            isPublic = false,
                            owner = OwnerDetails.App,
                            isUserSaved = false
                        )
                    ),
                )
            )
        }

        GamePackGrid(
            gamePacks = packs,
            onPackSelected = { displayablePack, isSelected ->
                packs = packs.map {
                    if (it.pack == displayablePack.pack) {
                        it.copy(isSelected = isSelected)
                    } else {
                        it
                    }
                }
            }
        )
    }
}
