package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.features.newgame.internal.presentation.model.PackOption
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.icon.CircleIcon
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun PackGrid(
    gamePacks: List<PackOption>,
    onPackSelected: (PackOption, Boolean) -> Unit,
) {
    NonLazyVerticalGrid(
        columns = 3,
        data = gamePacks,
        verticalSpacing = Dimension.D300,
        horizontalSpacing = Dimension.D300,
        itemContent = { index, gamePack ->
            Box {
                when (gamePack) {
                    is PackOption.CreatePack -> {
                        SpecialOption(
                            modifier = Modifier.bounceClick { onPackSelected(gamePack, false) },
                            text = "Create Your Own",
                            icon = SpyfallIcon.Add("")
                        )
                    }

                    is PackOption.EditYourPacks -> {
                        SpecialOption(
                            modifier = Modifier.bounceClick { onPackSelected(gamePack, false) },
                            text = "Edit Your Packs",
                            icon = SpyfallIcon.Pencil("")
                        )
                    }

                    is PackOption.Pack -> {
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
                            onClick = { isSelected -> onPackSelected(gamePack, isSelected) }
                        )
                    }
                }

                if (gamePack.new) {
                    NewBadge()
                }
            }
        }
    )
}

@Composable
private fun BoxScope.NewBadge() {
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

@Composable
private fun SpecialOption(
    modifier: Modifier = Modifier,
    text: String,
    icon: SpyfallIcon,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
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
            icon = icon,
            padding = Dimension.D50,
        )

        VerticalSpacerD500()

        Text(
            modifier = Modifier.padding(horizontal = Dimension.D300),
            text = text,
            textAlign = TextAlign.Center,
            typography = OddOneOutTheme.typography.Heading.H500
        )
    }
}


@Composable
@Preview
private fun PreviewGamePackGridCreatedPreviously() {
    Preview {
        var packs by remember {
            mutableStateOf(
                listOf(
                    PackOption.Pack(Pack.LocationPack.Fakes.Pack1),
                    PackOption.Pack(Pack.LocationPack.Fakes.Pack2),
                    PackOption.Pack(Pack.LocationPack.Fakes.Pack3),
                )
            )
        }

        PackGrid(
            gamePacks = packs,
            onPackSelected = { displayablePack, isSelected ->

                packs = packs.map {
                    if (it.pack == displayablePack.packData) {
                        it.copy(selected = isSelected)
                    } else {
                        it
                    }
                }
            }
        )
    }
}