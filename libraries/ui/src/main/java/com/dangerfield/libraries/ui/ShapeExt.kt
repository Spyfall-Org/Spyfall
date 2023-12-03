package spyfallx.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
