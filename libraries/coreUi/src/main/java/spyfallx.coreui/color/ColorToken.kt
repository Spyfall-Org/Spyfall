package spyfallx.coreui.color

import android.annotation.SuppressLint
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import spyfallx.coreui.Border
import spyfallx.coreui.Elevation
import spyfallx.coreui.Radii
import spyfallx.coreui.Radius
import spyfallx.coreui.StandardBorderWidth
import spyfallx.coreui.inset
import spyfallx.coreui.theme.SpyfallTheme
import spyfallx.coreui.thenIf
import spyfallx.coreui.thenIfNotNull
import androidx.compose.ui.graphics.Color as ComposeColor

@Immutable
sealed class ColorToken {
    internal abstract val name: String
    abstract val brush: Brush

    @Immutable
    @Suppress("MemberNameEqualsClassName")
    class Color internal constructor(
        override val name: String,
        internal val primitive: ColorPrimitive,
        val color: ComposeColor = primitive.color,
    ) : ColorToken() {
        private var _brush: SolidColor? = null
        override val brush: SolidColor get() = _brush ?: SolidColor(color).also { _brush = it }

        internal constructor(
            name: String,
            primitive: ColorPrimitive,
            alpha: Float,
        ) : this(
            name = name,
            primitive = primitive,
            color = primitive.color.copy(alpha = alpha)
        )

        override fun hashCode(): Int = color.hashCode()
        override fun equals(other: Any?): Boolean = this === other || other is Color && color == other.color
        override fun toString(): String = color.toString()

        companion object {
            /** Serves as a "default" color token. Is not meant to be used externally. */
            val Unspecified = Color("unspecified", ColorPrimitive.Unspecified)
        }
    }

    @Immutable
    class Gradient internal constructor(
        override val name: String,
        internal val primitive: ColorGradientPrimitive,
        val from: ComposeColor = primitive.from,
        val to: ComposeColor = primitive.to,
    ) : ColorToken() {
        private var _brush: Brush? = null
        override val brush: Brush get() = _brush ?: Brush.verticalGradient(listOf(from, to)).also { _brush = it }
        override fun hashCode(): Int = primitive.hashCode()
        override fun equals(other: Any?): Boolean = this === other || other is Gradient && primitive == other.primitive
        override fun toString(): String = primitive.toString()
    }
}

fun ColorToken.Color.asTint(): ColorFilter =
    ColorFilter.tint(color)

inline fun <T : ColorToken.Color?> T?.takeOrElse(block: () -> T): T =
    if (this != null && isSpecified) this else block()

val ColorToken.Color.isSpecified: Boolean get() = color.isSpecified

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.verticalFadingEdge(background: ColorToken.Color, direction: GradientDirection.Vertical): Modifier =
    inspectable(
        debugInspectorInfo {
            name = "verticalFadingEdge"
            properties["background"] = background
            properties["direction"] = direction
        }
    ) {
        composed { Modifier.background(direction.createBrush(background.color)) }
    }

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.horizontalFadingEdge(background: ColorToken.Color, direction: GradientDirection.Horizontal): Modifier =
    inspectable(
        debugInspectorInfo {
            name = "horizontalFadingEdge"
            properties["background"] = background
            properties["direction"] = direction
        }
    ) {
        composed { Modifier.background(direction.createBrush(background.color)) }
    }

sealed class GradientDirection(internal val createBrush: @Composable (ComposeColor) -> Brush) {
    sealed class Horizontal(isReverse: @Composable () -> Boolean) : GradientDirection({ color ->
        val reverse = isReverse()
        val start = if (reverse) color.copy(alpha = 0f) else color
        val end = if (reverse) color else color.copy(alpha = 0f)
        Brush.horizontalGradient(listOf(start, end))
    })

    sealed class Vertical(isReverse: Boolean) : GradientDirection({ color ->
        val start = if (isReverse) color.copy(alpha = 0f) else color
        val end = if (isReverse) color else color.copy(alpha = 0f)
        Brush.verticalGradient(listOf(start, end))
    })

    object LeftToRight : Horizontal({ false })
    object RightToLeft : Horizontal({ true })
    object StartToEnd : Horizontal({ LocalLayoutDirection.current == LayoutDirection.Rtl })
    object EndToStart : Horizontal({ LocalLayoutDirection.current == LayoutDirection.Ltr })
    object TopToBottom : Vertical(false)
    object BottomToTop : Vertical(true)
}

fun ColorToken.withAlpha(alpha: Float): ColorToken =
    when (this) {
        is ColorToken.Color -> withAlpha(alpha)
        is ColorToken.Gradient -> withAlpha(alpha)
    }

fun ColorToken.Color.withAlpha(alpha: Float): ColorToken.Color =
    ColorToken.Color(
        name = name,
        primitive = primitive,
        color = color.copy(alpha = alpha)
    )

fun ColorToken.Gradient.withAlpha(alpha: Float): ColorToken.Gradient =
    ColorToken.Gradient(
        name = name,
        primitive = primitive,
        from = from.copy(alpha = alpha),
        to = to.copy(alpha = alpha)
    )

@Stable
fun lerp(start: ColorToken.Color, stop: ColorToken.Color, fraction: Float): ColorToken.Color {
    val base = if (fraction < 0.5f) start else stop
    return ColorToken.Color(
        name = base.name,
        primitive = base.primitive,
        color = lerp(start.color, stop.color, fraction)
    )
}

@Stable
fun lerp(start: ColorToken.Gradient, stop: ColorToken.Gradient, fraction: Float): ColorToken.Gradient {
    val base = if (fraction < 0.5f) start else stop
    return ColorToken.Gradient(
        name = base.name,
        primitive = base.primitive,
        from = lerp(start.from, stop.from, fraction),
        to = lerp(start.to, stop.to, fraction)
    )
}

@Composable
fun animateColorTokenAsState(
    targetValue: ColorToken.Color,
    animationSpec: AnimationSpec<ColorToken.Color> = colorTokenColorDefaultSpring,
    label: String = "ColorAnimation",
    finishedListener: ((ColorToken) -> Unit)? = null,
): State<ColorToken.Color> {
    val converter = remember(targetValue) {
        val colorConverter = (ComposeColor.VectorConverter)(targetValue.color.colorSpace)
        TwoWayConverter(
            convertToVector = { token: ColorToken.Color ->
                colorConverter.convertToVector(token.color)
            },
            convertFromVector = { vector ->
                ColorToken.Color(
                    name = targetValue.name,
                    primitive = targetValue.primitive,
                    color = colorConverter.convertFromVector(vector)
                )
            }
        )
    }
    return animateValueAsState(
        targetValue = targetValue,
        typeConverter = converter,
        animationSpec = animationSpec,
        label = label,
        finishedListener = finishedListener
    )
}

@Composable
fun animateColorTokenAsState(
    targetValue: ColorToken.Gradient,
    animationSpec: AnimationSpec<ComposeColor> = colorDefaultSpring,
    label: String = "GradientAnimation",
): State<ColorToken.Gradient> {
    val fromColor by animateColorAsState(targetValue.from, animationSpec, label + "From")
    val toColor by animateColorAsState(targetValue.from, animationSpec, label + "To")
    return rememberUpdatedState(
        ColorToken.Gradient(
            targetValue.name,
            targetValue.primitive,
            fromColor,
            toColor
        )
    )
}

private val colorTokenColorDefaultSpring = spring<ColorToken.Color>()
private val colorDefaultSpring = spring<ComposeColor>()

fun Modifier.border(
    radius: Radius,
    color: ColorToken.Color = ColorToken.Color.Unspecified,
    width: Dp = StandardBorderWidth,
): Modifier = border(radius, Radius::shape, color, width)

fun Modifier.border(
    shape: Shape,
    color: ColorToken.Color = ColorToken.Color.Unspecified,
    width: Dp = StandardBorderWidth,
): Modifier = border(shape, { this }, color, width)

fun Modifier.border(radius: Radius, border: Border) =
    border(radius, border.color, border.width)

fun Modifier.border(shape: Shape, border: Border) =
    border(shape, border.color, border.width)

fun Modifier.background(
    color: ColorToken,
    radius: Radius = Radii.Default,
    elevation: Elevation = Elevation.None,
    shadowColor: ColorToken.Color = ColorToken.Color.Unspecified,
    clip: Boolean = elevation > Elevation.None,
    alpha: Float = 1f,
    border: Border? = null,
): Modifier = composed {
    val actualShadowColor = shadowColor.takeOrElse { SpyfallTheme.colorScheme.shadow }.color
    inspectable(
        debugInspectorInfo {
            name = "background"
            properties["color"] = color
            properties["radius"] = radius
            properties["elevation"] = elevation.dp
            properties["shadowColor"] = shadowColor
            properties["clip"] = clip
            properties["alpha"] = alpha
            properties["border"] = border
        }
    ) {
        val shape = radius.shape
        val backgroundShape = if (border == null) {
            shape
        } else {
            shape.inset(border.width / 2f)
        }
        then(
            when {
                elevation > Elevation.None || clip || alpha < 1f -> Modifier.graphicsLayer {
                    shadowElevation = elevation.dp.toPx()
                    spotShadowColor = actualShadowColor
                    ambientShadowColor = actualShadowColor
                    this.shape = shape
                    this.alpha = alpha
                    this.clip = clip
                }

                else -> Modifier
            }
        )
            .then(
                when (color) {
                    is ColorToken.Color -> Modifier.thenIf(color.isSpecified) { background(color.color, backgroundShape) }
                    is ColorToken.Gradient -> Modifier.background(color.brush, backgroundShape)
                }
            )
            .thenIfNotNull(value = border) {
                border(
                    width = it.width,
                    brush = it.color.brush,
                    shape = shape
                )
            }
    }
}

private inline fun <ShapeToken : Any> Modifier.border(
    shapeToken: ShapeToken,
    crossinline getShape: ShapeToken.() -> Shape,
    color: ColorToken.Color = ColorToken.Color.Unspecified,
    width: Dp = StandardBorderWidth,
): Modifier = composed {
    val resolvedColor = color.takeOrElse { SpyfallTheme.colorScheme.borderPrimary }
    inspectable(
        debugInspectorInfo {
            name = "border"
            value = resolvedColor
            properties["width"] = width
            properties["color"] = resolvedColor
            properties["shape"] = shapeToken
        }
    ) {
        border(
            width = width,
            color = resolvedColor.color,
            shape = getShape(shapeToken)
        )
    }
}
