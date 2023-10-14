package com.dangerfield.features.newgame.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dangerfield.spyfall.features.newgame.internal.R
import spyfallx.coreui.Elevation
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Radii
import spyfallx.coreui.Spacing
import spyfallx.coreui.clip
import spyfallx.coreui.color.AccentColor
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.color.background
import spyfallx.coreui.components.text.Text
import spyfallx.coreui.elevation
import spyfallx.coreui.theme.SpyfallTheme

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
                    .background(SpyfallTheme.colorScheme.surfacePrimary)
                    .padding(horizontal = Spacing.S300)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = type,
                    typographyToken = SpyfallTheme.typography.Heading.H500,
                    textAlign = TextAlign.Center,
                    color = SpyfallTheme.colorScheme.text
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
    Grid(
        columns = 3,
        items = gamePacks.size,
        modifier = Modifier.fillMaxWidth(),
        layoutItem = { modifier, index ->
            GamePackItem(
                colorPrimitive = AccentColor.values()
                    .let { it[index % it.size] }.colorPrimitive,
                modifier = modifier.padding(8.dp),
                type = gamePacks[index].type,
                number = gamePacks[index].number,
                onClick = {},
            )
        }
    )
}

@Composable
fun Grid(
    columns: Int,
    items: Int,
    layoutItem: @Composable (Modifier, Int)-> Unit,
    modifier: Modifier = Modifier
) {

    val rows = (items / columns) + if (items % columns != 0) 1 else 0

    Column(modifier) {
        for (rowIndex in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (colIndex in 0 until columns) {
                    val itemIndex = rowIndex * columns + colIndex
                    if (itemIndex < items) {
                        layoutItem(Modifier.weight(1f), itemIndex)
                    } else {
                        Spacer(modifier = Modifier.weight(1f)) // Empty space for missing items
                    }
                }
            }
        }
    }
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
private fun PreviewGamePackGrid() {
    PreviewContent(showBackground = true) {
        GamePackGrid(
            gamePacks = listOf(
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
                GamePack(number = "1", type = "Standard Pack"),
                GamePack(number = "1", type = "Standard Pack")
            )
        )
    }
}