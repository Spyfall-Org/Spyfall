@file:Suppress("MagicNumber")

package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dangerfield.libraries.ui.Point
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Radius
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.components.button.calculateCornerOffset
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.innerShadow
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R
import com.dangerfield.libraries.ui.thenIf

@Composable
fun GamePackItem(
    modifier: Modifier = Modifier,
    packName: String,
    number: Int?,
    isEnabled : Boolean = true,
    isSelected: Boolean = false,
    onClick: (isSelected: Boolean) -> Unit,
    colorResource: ColorResource,
) {
    Box(
        modifier = modifier
            .thenIf(isEnabled) {
                bounceClick { onClick(!isSelected) }
            }
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        var shouldPlayCheckAnimation by remember { mutableStateOf(false) }
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_animation))
        val numberText = number?.let { if (it > 1) " ($it)" else "" } ?: ""

        LaunchedEffect(isSelected) {
            if (isSelected) {
                shouldPlayCheckAnimation = true
            }
        }

        if (isEnabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 3.dp)
                    .clip(Radii.Card.shape)
                    .background(Color.Black.copy(alpha = 0.3f)) // Darker shade for the elevation effect
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(Radii.Card.shape)
                .background(if (isEnabled) colorResource.color else OddOneOutTheme.colors.surfaceDisabled.color)
                .thenIf(isEnabled) {
                    innerShadow(
                        cornersRadius = 10.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        spread = 0.dp,
                        offsetY = 5.dp,
                        offsetX = -5.dp
                    )
                }
                .semantics { role = Role.Button },
            contentAlignment = Alignment.Center
        ) {

            Text(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp),
                text = packName + numberText,
                typography = OddOneOutTheme.typography.Heading.H600,
                textAlign = TextAlign.Center,
                colorResource = if (isEnabled) colorResource.onColorResource else OddOneOutTheme.colors.onSurfaceDisabled,
            )
        }

        if (isEnabled) {
            DrawShineSquiggles(radius = Radii.Card)
        }

        if (isSelected && isEnabled) {
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(Radii.Card.shape)
                        .background(ColorResource.Black900.color.copy(alpha = 0.8f))
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
fun BoxScope.DrawShineSquiggles(radius: Radius = Radii.Card) {
    val density = LocalDensity.current

    Canvas(modifier = Modifier.matchParentSize()) {
        val width = this.size.width
        val height = this.size.height

        val cornerRadiusOffset = calculateCornerOffset(
            radius.cornerSize,
            density,
            width,
            height
        )

        val topRightSquiggle =
            Path().apply {
                val topRightStart = Point(
                    width - cornerRadiusOffset.x - 15.dp.toPx(),
                    cornerRadiusOffset.y.dp.toPx()
                )

                moveTo(topRightStart.x, topRightStart.y)

                val bottomOfSquiggle = topRightStart.offset(x = 10.dp.toPx(), y = 10.dp.toPx())

                val arcControl =
                    topRightStart.midpoint(bottomOfSquiggle).offset(x = 5.dp.toPx(), y = -4.dp.toPx())


                quadraticBezierTo(
                    arcControl.x, arcControl.y,
                    bottomOfSquiggle.x, bottomOfSquiggle.y
                )

                val control2 = arcControl.offset(-2.dp.toPx(), 1.dp.toPx())

                quadraticBezierTo(
                    control2.x, control2.y,
                    topRightStart.x, topRightStart.y
                )
            }

        val bottomLeftSquiggle = Path().apply {
            val bottomLeftStart = Point(
                cornerRadiusOffset.x + 10.dp.toPx(),
                height - cornerRadiusOffset.y
            )

            moveTo(bottomLeftStart.x, bottomLeftStart.y)
            val topOfSquiggle = bottomLeftStart.offset(x = -10.dp.toPx(), y = -10.dp.toPx())
            val arcControl =
                bottomLeftStart.midpoint(topOfSquiggle).offset(x = -5.dp.toPx(), y = 5.dp.toPx())

            quadraticBezierTo(
                arcControl.x, arcControl.y,
                topOfSquiggle.x, topOfSquiggle.y
            )

            val control2 = arcControl.offset(2.dp.toPx(), -2.dp.toPx())

            quadraticBezierTo(
                control2.x, control2.y,
                bottomLeftStart.x, bottomLeftStart.y
            )
        }

        drawPath(path = topRightSquiggle, color = Color.White)
        drawPath(path = bottomLeftSquiggle, color = Color.White)
    }
}

@Composable
@Preview
private fun PreviewGamePackItem() {
    Preview(showBackground = true) {
        Box(modifier = Modifier.padding(25.dp)) {
            GamePackItem(
                modifier = Modifier.height(100.dp),
                colorResource = ColorResource.GrapeJelly500,
                packName = "Standard  Extra Special Super Pack",
                onClick = {},
                number = 1
            )
        }
    }
}

@Composable
@Preview
private fun PreviewGamePackItemDisabled() {
    Preview(showBackground = true) {
        Box(modifier = Modifier.padding(25.dp)) {
            GamePackItem(
                modifier = Modifier.height(100.dp),
                colorResource = ColorResource.GrapeJelly500,
                packName = "Standard  Extra Special Super Pack",
                onClick = {},
                isEnabled = false,
                number = 1
            )
        }
    }
}

@Composable
@Preview
private fun PreviewGamePackItemSmall() {
    Preview(showBackground = true) {
        Box(modifier = Modifier.padding(25.dp)) {
            GamePackItem(
                modifier = Modifier.height(100.dp).width(80.dp),
                colorResource = ColorResource.GrapeJelly500,
                packName = "Standard  Extra Special Super Pack",
                onClick = {},
                number = 1
            )
        }
    }
}