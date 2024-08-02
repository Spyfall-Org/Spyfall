package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dangerfield.features.newgame.internal.presentation.model.NewGamePackOption
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.circleBackground
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.icon.CircleIcon
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.then
import com.dangerfield.libraries.ui.thenIf

@Composable
fun GamePackGrid(
    gamePacks: List<NewGamePackOption>,
    onPackSelected: (NewGamePackOption, Boolean) -> Unit,
    onCreateYourOwnSelected: () -> Unit,
    isCreateYourOwnNew: Boolean
) {
    val createYourOwnOption = NewGamePackOption(name = "Create your own pack")

    NonLazyVerticalGrid(
        columns = 3,
        data = gamePacks + listOf(createYourOwnOption),
        verticalSpacing = Dimension.D300,
        horizontalSpacing = Dimension.D300,
        itemContent = { index, gamePack ->
            if (gamePack == createYourOwnOption) {
                Box(modifier = Modifier.bounceClick(onClick = onCreateYourOwnSelected)) {
                    CreateYourOwnPackOption()
                    if (isCreateYourOwnNew) {
                        Box(
                            modifier = Modifier
                                .offset(x = 5.dp, y = -5.dp)
                                .background(
                                    OddOneOutTheme.colors.textWarning.color,
                                    shape = Radii.Card.shape
                                )
                                .align(Alignment.TopEnd)
                                .padding(
                                    vertical = Dimension.D50,
                                    horizontal = Dimension.D100
                                )
                        ) {
                            Text(
                                text = "New",
                                typography = OddOneOutTheme.typography.Heading.H500,
                            )
                        }
                    }
                }
            } else {
                GamePackItem(
                    modifier = Modifier
                        .fillMaxHeight()
                        .heightIn(min = 100.dp)
                        .thenIf(gamePack == createYourOwnOption) {
                            Modifier.border(
                                width = Dimension.D50,
                                color = OddOneOutTheme.colors.border.color,
                                shape = Radii.Card.shape
                            )
                        },
                    colorResource = if (gamePack == createYourOwnOption) {
                        OddOneOutTheme.colors.background
                    } else {
                        ThemeColor.entries.toTypedArray().let { it[index % it.size] }.colorResource
                    },
                    packName = gamePack.type,
                    isSelected = gamePack.isSelected,
                    isEnabled = gamePack.isEnabled,
                    number = gamePack.number?.toIntOrNull(),
                    onClick = {
                        onPackSelected(gamePack, it)
                    }
                )
            }
        }
    )
}

@Composable
private fun CreateYourOwnPackOption() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .heightIn(min = 100.dp)
            .border(
                width = Dimension.D50,
                color = OddOneOutTheme.colors.border.color,
                shape = Radii.Card.shape
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally,
    ) {
        CircleIcon(
            iconSize = IconSize.Small,
            backgroundColor = OddOneOutTheme.colors.onBackground,
            contentColor = OddOneOutTheme.colors.background,
            icon = SpyfallIcon.Add("Create Pack"),
            padding = Dimension.D25,
        )

        VerticalSpacerD500()

        Text(
            text = "Create your own pack",
            textAlign = TextAlign.Center,
            typography = OddOneOutTheme.typography.Heading.H500
        )
    }
}

@Composable
@Preview
private fun PreviewGamePackGrid() {
    Preview {
        var packs by remember {
            mutableStateOf(
                listOf(
                    NewGamePackOption(
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

                    NewGamePackOption(
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

                    NewGamePackOption(
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
                    NewGamePackOption(
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
            onCreateYourOwnSelected = {},
            isCreateYourOwnNew = true,
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
