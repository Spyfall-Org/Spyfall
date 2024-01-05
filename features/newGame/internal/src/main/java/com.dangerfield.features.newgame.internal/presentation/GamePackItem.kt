@file:Suppress("MagicNumber")

package com.dangerfield.features.newgame.internal.presentation

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
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.elevation
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.spyfall.libraries.resources.R
import spyfallx.ui.color.background

@Composable
fun GamePackItem(
    modifier: Modifier = Modifier,
    number: String,
    type: String,
    isSelected: Boolean = false,
    onClick: (isSelected: Boolean) -> Unit,
    colorPrimitive: ColorPrimitive,
) {
    Box(
        modifier = modifier
            .elevation(
                elevation = Elevation.Fixed,
                shape = Radii.Card.shape,
                clip = true,
                shadowColor = OddOneOutTheme.colorScheme.shadow
            )
            .fillMaxWidth()
            .clickable {
                onClick(!isSelected)
            }
    ) {

        var shouldPlayCheckAnimation by remember { mutableStateOf(false) }

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_animation))

        LaunchedEffect(isSelected) {
            if (isSelected) {
                shouldPlayCheckAnimation = true
            }
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(OddOneOutTheme.colorScheme.surfaceSecondary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorPrimitive),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = number,
                    typographyToken = OddOneOutTheme.typography.Display.D1100,
                    colorPrimitive = colorPrimitive.onColorPrimitive
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .padding(horizontal = Spacing.S200)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = type,
                    typographyToken = OddOneOutTheme.typography.Heading.H600,
                    textAlign = TextAlign.Center,
                    color = OddOneOutTheme.colorScheme.text,
                )
            }
        }

        if (isSelected) {
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorPrimitive.Black900.color.copy(alpha = 0.8f))
                )
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
@Preview
private fun PreviewGamePackItem() {
    PreviewContent {
        GamePackItem(
            modifier = Modifier.height(100.dp),
            colorPrimitive = ColorPrimitive.GrapeJelly500,
            number = "1",
            type = "Standard  Extra Special Super Pack",
            onClick = {},
        )
    }
}