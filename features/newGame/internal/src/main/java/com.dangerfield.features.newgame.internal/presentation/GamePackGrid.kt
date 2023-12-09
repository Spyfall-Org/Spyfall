package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.Spacing
import spyfallx.ui.color.AccentColor

@Composable
fun GamePackGrid(
    gamePacks: List<DisplayablePack>,
    onPackSelected: (DisplayablePack, Boolean) -> Unit
) {
    NonLazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = 3,
        data = gamePacks,
        verticalSpacing = Spacing.S300,
        horizontalSpacing = Spacing.S300,
        itemContent = { index, gamePack ->
            GamePackItem(
                colorPrimitive = AccentColor.entries.toTypedArray()
                    .let { it[index % it.size] }.colorPrimitive,
                type = gamePack.type,
                number = gamePack.number,
                isSelected = gamePack.isSelected,
                onClick = {
                    onPackSelected(gamePack, it)
                }
            )
        }
    )
}

@Composable
@ThemePreviews
private fun PreviewGamePackGrid() {
    PreviewContent {
        var packs by remember {
            mutableStateOf(
                listOf(
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 1", locations = listOf())
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 2", locations = listOf())
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 3", locations = listOf())
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Special 4", locations = listOf())
                    ),
                )
            )
        }

        GamePackGrid(
            gamePacks = packs,
            onPackSelected = { displayablePack, isSelected ->
                packs = packs.map {
                    if(it.pack == displayablePack.pack) {
                        it.copy(isSelected = isSelected)
                    } else {
                        it
                    }
                }
            }
        )
    }
}
