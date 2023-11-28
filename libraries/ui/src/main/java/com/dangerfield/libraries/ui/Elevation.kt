@file:Suppress("MagicNumber")
package spyfallx.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.color.ColorToken
import spyfallx.ui.color.background
import spyfallx.ui.color.takeOrElse
import com.dangerfield.libraries.ui.theme.SpyfallTheme


@Immutable
@JvmInline
value class Elevation internal constructor(val dp: Dp) : Comparable<Elevation> {

    override fun compareTo(other: Elevation): Int = dp.compareTo(other.dp)

    companion object {
        val None = Elevation(0.dp)
        val Inline = Elevation(1.dp)
        val Fixed = Elevation(6.dp)
        val AppBar = Elevation(4.dp)


        internal val VectorConverter: TwoWayConverter<Elevation, AnimationVector1D> = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.dp.value) },
            convertFromVector = { Elevation(Dp(it.value)) }
        )
    }
}

/**
 * Linearly interpolate between two [Elevation]s.
 *
 * The [fraction] argument represents position on the timeline, with 0.0 meaning
 * that the interpolation has not started, returning [start] (or something
 * equivalent to [start]), 1.0 meaning that the interpolation has finished,
 * returning [stop] (or something equivalent to [stop]), and values in between
 * meaning that the interpolation is at the relevant point on the timeline
 * between [start] and [stop]. The interpolation can be extrapolated beyond 0.0 and
 * 1.0, so negative values and values greater than 1.0 are valid.
 *
 * The returned elevation should only be used transiently such as in an animation. You should not use this to create
 * new elevations that don't exist in the [Elevation] companion because we want to limit the number of elevations we
 * use.
 */
fun lerp(start: Elevation, stop: Elevation, fraction: Float): Elevation = Elevation(lerp(start.dp, stop.dp, fraction))

fun Modifier.elevation(
    elevation: Elevation,
    shape: Shape,
    clip: Boolean = elevation > Elevation.None,
    shadowColor: ColorToken.Color = ColorToken.Color.Unspecified,
): Modifier = elevation(
    elevation = elevation,
    radius = shape,
    radiusToShape = { this },
    clip = clip,
    shadowColor = shadowColor
)

fun Modifier.elevation(
    elevation: Elevation,
    radius: Radius,
    clip: Boolean = elevation > Elevation.None,
    shadowColor: ColorToken.Color = ColorToken.Color.Unspecified,
): Modifier = elevation(
    elevation = elevation,
    radius = radius,
    radiusToShape = Radius::shape,
    clip = clip,
    shadowColor = shadowColor
)

@Composable
fun animateElevationAsState(
    targetValue: Elevation,
    animationSpec: AnimationSpec<Elevation> = elevationDefaultSpring,
    label: String = "Elevation",
    finishedListener: ((Elevation) -> Unit)? = null,
): State<Elevation> = animateValueAsState(
    targetValue,
    Elevation.VectorConverter,
    animationSpec,
    label = label,
    finishedListener = finishedListener
)

private inline fun <S : Any> Modifier.elevation(
    elevation: Elevation,
    radius: S,
    crossinline radiusToShape: S.() -> Shape,
    clip: Boolean = elevation > Elevation.None,
    shadowColor: ColorToken.Color = ColorToken.Color.Unspecified,
): Modifier = composed {
    val color = shadowColor.takeOrElse { SpyfallTheme.colorScheme.shadow }
    inspectable(
        debugInspectorInfo {
            name = "elevation"
            properties["elevation"] = elevation
            properties["radius"] = radius
            properties["clip"] = clip
            properties["shadowColor"] = color
        }
    ) {
        shadow(
            elevation = elevation.dp,
            shape = radiusToShape(radius),
            clip = clip,
            ambientColor = color.color,
            spotColor = color.color
        )
    }
}

private val elevationDefaultSpring = spring(visibilityThreshold = Elevation(Dp.VisibilityThreshold))

@Preview(widthDp = 1400)
@Composable
private fun ElevationPreview() {
    val elevations = listOf(Elevation::None, Elevation::Inline, Elevation::Fixed)
    PreviewContent(showBackground = true) {
        Column(Modifier.padding(Spacing.S900)) {
            Row {
                elevations.forEachIndexed { index, elevation ->
                    if (index > 0) Spacer(Modifier.width(Spacing.S900))
                    Box(Modifier.weight(1f)) {
                        Text(
                            "elevation-${elevation.name.lowercase()}",
                            style = SpyfallTheme.typography.Heading.H800.style,
                            color = SpyfallTheme.colorScheme.text.color
                        )
                    }
                }
            }
            Spacer(Modifier.height(Spacing.S900))
            Row {
                elevations.forEachIndexed { index, elevation ->
                    if (index > 0) Spacer(Modifier.width(Spacing.S900))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(SpyfallTheme.colorScheme.surfaceSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        val size = when (elevation()) {
                            Elevation.None -> Modifier.fillMaxSize(0.5f)
                            Elevation.Inline ->
                                Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(64.dp)

                            Elevation.Fixed -> Modifier.size(64.dp)
                            else -> error("Unknown elevation $elevation")
                        }
                        val radius = when (elevation()) {
                            Elevation.None -> Radii.Default
                            Elevation.Inline -> Radii.Banner
                            Elevation.Fixed -> Radii.Fab
                            else -> error("Unknown elevation $elevation")
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    SpyfallTheme.colorScheme.surfacePrimary,
                                    radius = radius,
                                    elevation = elevation()
                                )
                                .then(size)
                        )
                    }
                }
            }
        }
    }
}
