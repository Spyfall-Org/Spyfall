package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.PreviewContent
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.oddoneoout.features.newgame.internal.R

@Composable
fun GamePackGrid(
    gamePacks: List<DisplayablePack>,
    onPackSelected: (DisplayablePack, Boolean) -> Unit
) {
    val packs = gamePacks + DisplayablePack(
        isSelected = false,
        isEnabled = false,
        pack = Pack(
            name = dictionaryString(R.string.create_your_own_coming_soon),
            locations = listOf()
        )
    )

    NonLazyVerticalGrid(
        columns = 3,
        data = packs,
        verticalSpacing = Spacing.S300,
        horizontalSpacing = Spacing.S300,
        itemContent = { index, gamePack ->
            GamePackItem(
                modifier = Modifier
                    .fillMaxHeight()
                    .heightIn(min = 100.dp),
                colorPrimitive = ThemeColor.entries.toTypedArray()
                    .let { it[index % it.size] }.colorPrimitive,
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
