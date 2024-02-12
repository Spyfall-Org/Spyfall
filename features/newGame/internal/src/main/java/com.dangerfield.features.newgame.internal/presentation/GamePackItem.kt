@file:Suppress("MagicNumber")

package com.dangerfield.features.newgame.internal.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.Point
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Radius
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.button.calculateCornerOffset
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.elevation
import com.dangerfield.libraries.ui.innerShadow
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.newgame.internal.R
import spyfallx.ui.color.background
import spyfallx.ui.then

@Composable
fun GamePackItem(
    modifier: Modifier = Modifier,
    packName: String,
    isSelected: Boolean = false,
    onClick: (isSelected: Boolean) -> Unit,
    colorPrimitive: ColorPrimitive,
) {
    Box(
        modifier = modifier
            .bounceClick { onClick(!isSelected) }
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        var shouldPlayCheckAnimation by remember { mutableStateOf(false) }

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_animation))

        LaunchedEffect(isSelected) {
            if (isSelected) {
                shouldPlayCheckAnimation = true
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 3.dp)
                .clip(Radii.Card.shape)
                .background(Color.Black.copy(alpha = 1f)) // Darker shade for the elevation effect
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(Radii.Card.shape)
                .background(colorPrimitive)
                .innerShadow(
                    cornersRadius = 10.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    spread = 0.dp,
                    offsetY = 5.dp,
                    offsetX = -5.dp
                )
                .semantics { role = Role.Button },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = packName,
                typographyToken = OddOneOutTheme.typography.Heading.H600,
                textAlign = TextAlign.Center,
                colorPrimitive = colorPrimitive.onColorPrimitive,
            )
        }

        drawShineSquiggles(radius = Radii.Card)

        if (isSelected) {
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(Radii.Card.shape)
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
fun BoxScope.drawShineSquiggles(radius: Radius = Radii.Card) {
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
                    width - cornerRadiusOffset.x - 20.dp.toPx(),
                    cornerRadiusOffset.y.dp.toPx()
                )

                moveTo(topRightStart.x, topRightStart.y)

                val bottomOfSquiggle = topRightStart.offset(x = 10.dp.toPx(), y = 15.dp.toPx())

                val arcControl =
                    topRightStart.midpoint(bottomOfSquiggle).offset(x = 15.dp.toPx(), y = -10.dp.toPx())


                quadraticBezierTo(
                    arcControl.x, arcControl.y,
                    bottomOfSquiggle.x, bottomOfSquiggle.y
                )

                val control2 = arcControl.offset(-2.dp.toPx(), 4.dp.toPx())

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
    PreviewContent(showBackground = true) {
        Box(modifier = Modifier.padding(25.dp)) {
            GamePackItem(
                modifier = Modifier.height(100.dp),
                colorPrimitive = ColorPrimitive.GrapeJelly500,
                packName = "Standard  Extra Special Super Pack",
                onClick = {},
            )
        }
    }
}