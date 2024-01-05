package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.color.ThemeColor

@Composable
fun GamePackGrid(
    gamePacks: List<DisplayablePack>,
    onPackSelected: (DisplayablePack, Boolean) -> Unit
) {
    NonLazyVerticalGrid(
        columns = 3,
        data = gamePacks,
        verticalSpacing = Spacing.S300,
        horizontalSpacing = Spacing.S300,
        itemContent = { index, gamePack ->
            GamePackItem(
                modifier = Modifier.fillMaxHeight(),
                colorPrimitive = ThemeColor.entries.toTypedArray()
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
                        pack = Pack(name = "Super Duper extra Special Pack 1", locations = listOf())
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Super Special Pack 2", locations = listOf())
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Super Special Pack 3", locations = listOf())
                    ),
                    DisplayablePack(
                        isSelected = false,
                        pack = Pack(name = "Super Special Pack 4", locations = listOf())
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
