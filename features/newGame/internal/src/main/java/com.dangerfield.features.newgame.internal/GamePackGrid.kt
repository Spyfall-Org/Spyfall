@file:Suppress("MagicNumber")
package com.dangerfield.features.newgame.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dangerfield.spyfall.features.newgame.internal.R
import spyfallx.ui.Elevation
import spyfallx.ui.PreviewContent
import spyfallx.ui.Radii
import spyfallx.ui.Spacing
import spyfallx.ui.color.AccentColor
import spyfallx.ui.color.ColorPrimitive
import spyfallx.ui.color.background
import spyfallx.ui.components.NonLazyVerticalGrid
import spyfallx.ui.components.text.Text
import spyfallx.ui.elevation
import spyfallx.ui.theme.SpyfallTheme

@Composable
private fun GamePackItem(
    number: String,
    type: String,
    onClick: (isSelected: Boolean) -> Unit,
    colorPrimitive: ColorPrimitive,
    modifier: Modifier = Modifier
) {
    var isSelected by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .elevation(
                elevation = Elevation.Fixed,
                shape = Radii.Card.shape,
                clip = true,
                shadowColor = SpyfallTheme.colorScheme.shadow
            )
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                isSelected = !isSelected
                onClick(isSelected)
            }
    ) {

        var shouldPlayCheckAnimation by remember { mutableStateOf(false) }

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_animation))

        LaunchedEffect(isSelected) {
            if (isSelected) {
                shouldPlayCheckAnimation = true
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
                    .background(colorPrimitive),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = number,
                    typographyToken = SpyfallTheme.typography.Display.D1100,
                    colorPrimitive = colorPrimitive.onColorPrimitive
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxWidth()
                    .background(SpyfallTheme.colorScheme.surfaceSecondary)
                    .padding(horizontal = Spacing.S300)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = type,
                    typographyToken = SpyfallTheme.typography.Heading.H400,
                    textAlign = TextAlign.Center,
                    color = SpyfallTheme.colorScheme.text,
                    maxLines = 2
                )
            }
        }

        if (isSelected) {
            Box {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(ColorPrimitive.Black900.color.copy(alpha = 0.8f)))
                LottieAnimation(
                    modifier = Modifier.fillMaxSize(),
                    composition = composition,
                    isPlaying = shouldPlayCheckAnimation,
                    speed = 1.5f
                )
            }
        }
    }
}

@Composable
fun GamePackGrid(gamePacks: List<GamePack>) {
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
                onClick = {},
            )
        }
    )
}

@Composable
@Preview
private fun PreviewGamePackItem() {
    PreviewContent {
        GamePackItem(
            colorPrimitive = ColorPrimitive.GrapeJelly500,
            number = "1",
            type = "Standard Pack",
            onClick = {},
        )
    }
}

@Composable
@Preview
private fun PreviewGamePackGridDark() {
    PreviewContent(showBackground = true, isDarkMode = true) {
        GamePackGrid(
            gamePacks = listOf(
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
            )
        )
    }
}

@Composable
@Preview
private fun PreviewGamePackGrid() {
    PreviewContent(showBackground = true) {
        GamePackGrid(
            gamePacks = listOf(
                GamePack(number = "1", type = "Standard Pack But longer"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack")
            )
        )
    }
}
