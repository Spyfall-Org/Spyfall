package spyfallx.coreui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.color.background
import spyfallx.coreui.theme.SpyfallTheme
import spyfallx.coreui.typography.Typography


@Immutable
class Radius private constructor(val shape: RoundedCornerShape) {
    internal constructor(cornerSize: CornerSize) : this(RoundedCornerShape(cornerSize))

    val cornerSize: CornerSize
        get() = shape.topStart.takeUnless { it == SquareCornerSize }
            ?: shape.topEnd.takeUnless { it == SquareCornerSize }
            ?: shape.bottomEnd.takeUnless { it == SquareCornerSize }
            ?: shape.bottomStart

    /**
     * Returns a new [Radius] where only a subset of the corners are set. This is useful for things like a
     * half-sheet where only the top corners are rounded.
     */
    fun withSides(
        topStart: Boolean = false,
        topEnd: Boolean = false,
        bottomEnd: Boolean = false,
        bottomStart: Boolean = false,
    ): Radius {
        require(topStart || topEnd || bottomEnd || bottomStart) { "At least one side must be true" }
        return Radius(
            RoundedCornerShape(
                topStart = if (topStart) cornerSize else CornerSize(0.dp),
                topEnd = if (topEnd) cornerSize else CornerSize(0.dp),
                bottomEnd = if (bottomEnd) cornerSize else CornerSize(0.dp),
                bottomStart = if (bottomStart) cornerSize else CornerSize(0.dp)
            )
        )
    }

    override fun equals(other: Any?): Boolean = this === other || other is Radius && shape == other.shape
    override fun hashCode(): Int = shape.hashCode()
    override fun toString(): String = "Radius(cornerSize=$cornerSize)"
}

object Radii {
    val Round = Radius(CornerSize(percent = 50))
    val R400 = Radius(CornerSize(Sizes.S400))
    val R300 = Radius(CornerSize(Sizes.S300))
    val R100 = Radius(CornerSize(Sizes.S100))
    val None = Radius(SquareCornerSize)

    val Default get() = None
    val Button get() = Round
    val IconButton get() = Round
    val Banner get() = R400
    val Modal get() = R400
    val Note get() = R300
    val Fab get() = Round
    val Navigation get() = None
    val Header get() = None
    val HalfSheet = R400.withSides(topStart = true, topEnd = true)
    val Card get() = R400
    val DropDownMenu get() = R300
    val RectangularBadge get() = R100
    val PillBadge get() = Round
}

fun Modifier.clip(radius: Radius): Modifier = clip(radius.shape)

private val SquareCornerSize = CornerSize(0.dp)

@Preview(widthDp = 1000)
@Composable
private fun PreviewRadii() {
    val radiusList = listOf(
        Radii.Round to "radius-round",
        Radii.R400 to "radius-400",
        Radii.R300 to "radius-300",
        Radii.R100 to "radius-100",
        Radii.None to "radius-none"
    )

    PreviewContent(showBackground = true) {
        Row(Modifier.fillMaxWidth()) {
            for ((radii, name) in radiusList) {
                Column(
                    Modifier
                        .padding(Spacing.S500)
                        .weight(1f)
                ) {
                    Text(
                        text = name,
                        style = SpyfallTheme.typography.Heading.H800.style,
                        color = ColorPrimitive.Black800.color
                    )
                    Spacer(modifier = Modifier.height(Spacing.S800))
                    Box(
                        modifier = Modifier
                            .aspectRatio(0.75f)
                            .fillMaxWidth()
                            .background(SpyfallTheme.colorScheme.surfaceSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        if (radii == Radii.Round) {
                            RoundPreview(radii)
                        } else {
                            RoundedPreview(radii)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundPreview(radius: Radius) {
    Box(
        Modifier
            .padding(52.dp)
            .aspectRatio(1f)
            .fillMaxSize()
            .background(SpyfallTheme.colorScheme.textAccentPrimary, radius)
    )
}

@Composable
private fun RoundedPreview(radius: Radius) {
    Box(
        Modifier
            .padding(top = 52.dp, start = 52.dp)
            .background(ColorPrimitive.CherryPop700.color, radius.shape)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    color = SpyfallTheme.colorScheme.textAccentPrimary,
                    radius = radius.withSides(topStart = true)
                )
        )
    }
}
