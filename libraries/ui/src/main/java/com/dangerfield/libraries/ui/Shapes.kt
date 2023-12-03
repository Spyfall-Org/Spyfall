package com.dangerfield.libraries.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import com.dangerfield.libraries.ui.translate

fun Shape.inset(amount: Dp): Shape = inset(
    start = amount,
    top = amount,
    end = amount,
    bottom = amount
)

fun Shape.inset(horizontal: Dp = 0.dp, vertical: Dp = 0.dp) = inset(
    start = horizontal,
    top = vertical,
    end = horizontal,
    bottom = vertical
)

fun Shape.inset(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): Shape = InsetShape(delegate = this, start = start, top = top, end = end, bottom = bottom)

fun Shape.withSize(
    size: Dp,
    constrain: Boolean = true,
): Shape = SizedShape(this, size, size, constrain)

fun Shape.withSize(
    size: DpSize,
    constrain: Boolean = true,
): Shape = SizedShape(this, size.width, size.height, constrain)

fun Shape.withSize(
    width: Dp,
    height: Dp,
    constrain: Boolean = true,
): Shape = SizedShape(this, width, height, constrain)

fun Shape.withWidth(
    width: Dp,
    constrain: Boolean = true,
): Shape = SizedShape(this, width, Dp.Unspecified, constrain)

fun Shape.withHeight(
    height: Dp,
    constrain: Boolean = true,
): Shape = SizedShape(this, Dp.Unspecified, height, constrain)

fun Shape.align(alignment: Alignment): Shape = AlignedShape(this, alignment)

operator fun Shape.plus(other: Shape): Shape = CombinedShape(this, other)

private class InsetShape(
    private val delegate: Shape,
    private val start: Dp,
    private val top: Dp,
    private val end: Dp,
    private val bottom: Dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        with(density) {
            val start = start.toPx()
            val top = top.toPx()
            val end = end.toPx()
            val bottom = bottom.toPx()
            val insetSize = size.copy(
                width = size.width - start - end,
                height = size.height - top - bottom
            )
            return delegate.createOutline(insetSize, layoutDirection, density).translate(start, top)
        }
}

private class CombinedShape(
    private val first: Shape,
    private val second: Shape,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        first.createOutline(size, layoutDirection, density) + second.createOutline(size, layoutDirection, density)
}

private class SizedShape(
    private val delegate: Shape,
    private val width: Dp,
    private val height: Dp,
    private val constrain: Boolean,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = with(density) {
        delegate.createOutline(
            size = Size(
                width = when {
                    width.isUnspecified -> size.width
                    constrain -> width.toPx().coerceAtMost(size.width)
                    else -> width.toPx()
                },
                height = when {
                    height.isUnspecified -> size.height
                    constrain -> height.toPx().coerceAtMost(size.width)
                    else -> height.toPx()
                }
            ),
            layoutDirection = layoutDirection,
            density = density
        )
    }
}

private class AlignedShape(
    private val delegate: Shape,
    private val alignment: Alignment,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val outline = delegate.createOutline(size, layoutDirection, density)

        return outline.translate(
            alignment.align(
                outline.size(),
                size,
                layoutDirection
            )
        )
    }
}

@Preview
@Composable
private fun ShapesPreview() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                Color.Red,
                RoundedCornerShape(10.dp).inset(top = 20.dp) + CircleShape.withSize(40.dp).align(Alignment.TopCenter)
            )
    )
}
