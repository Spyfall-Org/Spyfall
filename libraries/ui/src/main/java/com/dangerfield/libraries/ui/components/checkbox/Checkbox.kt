package com.dangerfield.libraries.ui.components.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.dangerfield.libraries.ui.HorizontalSpacerS800
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Sizes
import com.dangerfield.libraries.ui.StandardBorderWidth
import com.dangerfield.libraries.ui.components.checkbox.Checkbox.CheckboxDefaults.borderDisabledColor
import com.dangerfield.libraries.ui.components.checkbox.Checkbox.CheckboxDefaults.borderEnabledColor
import com.dangerfield.libraries.ui.components.checkbox.Checkbox.CheckboxDefaults.checkDisabledColor
import com.dangerfield.libraries.ui.components.checkbox.Checkbox.CheckboxDefaults.checkEnabledColor
import com.dangerfield.libraries.ui.components.checkbox.Checkbox.borderColor
import com.dangerfield.libraries.ui.components.checkbox.Checkbox.boxColor
import com.dangerfield.libraries.ui.components.checkbox.Checkbox.checkmarkColor
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.ui.color.ColorToken
import java.util.Locale
import kotlin.math.floor
import kotlin.math.max

@Composable
fun Checkbox(
    state: CheckboxState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    key(state.animationKey) {
        Checkbox(
            checked = state.checked,
            onCheckedChange = state::onClicked,
            modifier = modifier,
            enabled = enabled,
            interactionSource = interactionSource
        )
    }
}

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val value = ToggleableState(checked)
    val transition = updateTransition(targetState = value, label = "State")
    val checkDrawFraction = transition.animateFloat(
        transitionSpec = {
            when {
                initialState == ToggleableState.Off -> tween(Checkbox.CheckAnimationDuration)
                targetState == ToggleableState.Off -> snap(Checkbox.BoxOutDuration)
                else -> spring()
            }
        },
        label = "CheckDrawFraction"
    ) {
        when (it) {
            ToggleableState.On -> 1f
            ToggleableState.Off -> 0f
            ToggleableState.Indeterminate -> 1f
        }
    }
    val checkCenterGravitationShiftFraction = transition.animateFloat(
        transitionSpec = {
            when {
                initialState == ToggleableState.Off -> snap()
                targetState == ToggleableState.Off -> snap(Checkbox.BoxOutDuration)
                else -> tween(durationMillis = Checkbox.CheckAnimationDuration)
            }
        },
        label = "CheckCenterGravitationShiftFraction"
    ) {
        when (it) {
            ToggleableState.On -> 0f
            ToggleableState.Off -> 0f
            ToggleableState.Indeterminate -> 1f
        }
    }
    val checkCache = remember { CheckDrawingCache() }
    val checkColor = value.checkmarkColor(enabled)
    val boxColor = value.boxColor(enabled)
    val borderColor = value.borderColor(enabled)
    Canvas(
        modifier
            .triStateToggleable(
                state = ToggleableState(checked),
                onClick = { onCheckedChange(!checked) },
                enabled = true,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = Checkbox.RippleRadius
                )
            )
            .padding(Checkbox.OuterPadding)
            .wrapContentSize(Alignment.Center)
            .requiredSize(Checkbox.CheckSize + Checkbox.InnerPadding)
    ) {
        drawBox(
            boxColor = boxColor.value,
            borderColor = borderColor.value,
            radius = Checkbox.Radius.cornerSize.toPx(size, this),
            strokeWidth = floor(Checkbox.BorderWidth.toPx())
        )
        inset(Checkbox.InnerPadding.toPx()) {
            drawCheck(
                checkColor = checkColor.value,
                checkFraction = checkDrawFraction.value,
                crossCenterGravitation = checkCenterGravitationShiftFraction.value,
                strokeWidthPx = floor(Checkbox.CheckStrokeWidth.toPx()),
                drawingCache = checkCache
            )
        }
    }
}

object Checkbox {
    internal val RippleRadius = Sizes.S1200 / 2
    internal val OuterPadding = Sizes.S300
    internal val Radius = Radii.Checkbox
    internal val BorderWidth = StandardBorderWidth
    internal val CheckStrokeWidth = 2.71.dp
    internal val InnerPadding = Sizes.S50
    internal val CheckSize = Sizes.S700

    private const val BoxInDuration = 150
    internal const val BoxOutDuration = 300
    internal const val CheckAnimationDuration = 300

    object CheckboxDefaults {
        val checkDisabledColor: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.background
        val borderDisabledColor: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.textDisabled

        val checkEnabledColor: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.background
        val borderEnabledColor: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.accent
    }

    @Composable
    internal fun ToggleableState.checkmarkColor(enabled: Boolean): State<Color> {
        val targetColor = (if (enabled) checkDisabledColor else checkEnabledColor)
            .color
            .copy(
                alpha = when (this) {
                    ToggleableState.On, ToggleableState.Indeterminate -> 1f
                    ToggleableState.Off -> 0f
                }
            )
        val duration = CheckAnimationDuration
        return animateColorAsState(targetColor, tween(durationMillis = duration), label = "CheckmarkColor")
    }

    @Composable
    internal fun ToggleableState.boxColor(enabled: Boolean): State<Color> {
        val targetColor = targetBorderColor(enabled).copy(
            alpha = when (this) {
                ToggleableState.On, ToggleableState.Indeterminate -> 1f
                ToggleableState.Off -> 0f
            }
        )
        val duration = if (this == ToggleableState.Off) BoxOutDuration else BoxInDuration
        return animateColorAsState(targetColor, tween(durationMillis = duration), label = "BoxColor")
    }

    @Composable
    internal fun ToggleableState.borderColor(enabled: Boolean): State<Color> {
        val duration = if (this == ToggleableState.Off) BoxOutDuration else BoxInDuration
        return animateColorAsState(targetBorderColor(enabled), tween(durationMillis = duration), label = "BorderColor")
    }

    @Composable
    private fun ToggleableState.targetBorderColor(enabled: Boolean): Color = when (this) {
        ToggleableState.On, ToggleableState.Indeterminate ->
            if (enabled)  borderEnabledColor else borderDisabledColor

        ToggleableState.Off ->
            if (enabled)  borderEnabledColor else borderDisabledColor
    }.color
}

private fun DrawScope.drawBox(
    boxColor: Color,
    borderColor: Color,
    radius: Float,
    strokeWidth: Float,
) {
    val halfStrokeWidth = strokeWidth / 2.0f
    val stroke = Stroke(strokeWidth)
    val checkboxSize = size.width
    if (boxColor == borderColor) {
        drawRoundRect(
            boxColor,
            size = Size(checkboxSize, checkboxSize),
            cornerRadius = CornerRadius(radius),
            style = Fill
        )
    } else {
        drawRoundRect(
            boxColor,
            topLeft = Offset(strokeWidth, strokeWidth),
            size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
            cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
            style = Fill
        )
        drawRoundRect(
            borderColor,
            topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
            size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
            cornerRadius = CornerRadius(radius - halfStrokeWidth),
            style = stroke
        )
    }
}

private fun DrawScope.drawCheck(
    checkColor: Color,
    checkFraction: Float,
    crossCenterGravitation: Float,
    strokeWidthPx: Float,
    drawingCache: CheckDrawingCache,
) {
    val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
    val width = size.width
    // See computeCheckmarkValues for these values
    val startX = 0.12f
    val startY = 0.51f
    val turnX = 0.35f
    val turnY = 0.75f
    val endX = 0.88f
    val endY = 0.19f

    val gravitatedCrossX = lerp(turnX, 0.5f, crossCenterGravitation)
    val gravitatedCrossY = lerp(turnY, 0.5f, crossCenterGravitation)
    // gravitate only Y for end to achieve center line
    val gravitatedLeftY = lerp(startY, 0.5f, crossCenterGravitation)
    val gravitatedRightY = lerp(endY, 0.5f, crossCenterGravitation)

    with(drawingCache) {
        checkPath.reset()
        checkPath.moveTo(width * startX, width * gravitatedLeftY)
        checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
        checkPath.lineTo(width * endX, width * gravitatedRightY)
        pathMeasure.setPath(checkPath, false)
        pathToDraw.reset()
        pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
    }
    drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
    val checkPath: Path = Path(),
    val pathMeasure: PathMeasure = PathMeasure(),
    val pathToDraw: Path = Path(),
)

/**
 * A function that is used to automate the process of computing the values for the checkmark (start, turn and end)
 *
 * This is called from a dummy test
 *
 * @param viewportWidth the width of the viewport from the SVG/VectorDrawable
 * @param viewportHeight the height of the viewport from the SVG/VectorDrawable
 * @param pathData the path data
 */
@Suppress("unused")
internal fun computeCheckmarkValues(
    viewportWidth: Float,
    viewportHeight: Float,
    pathData: String,
) {
    fun computeCenter(a: Offset, b: Offset) = Offset((a.x + b.x) / 2 / viewportWidth, (a.y + b.y) / 2 / viewportHeight)

    val xs = mutableListOf<Float>()
    val ys = mutableListOf<Float>()
    val pattern = Regex("""([A-Za-z])(\d+(?:\.\d+)?) (\d+(?:\.\d+)?)""")
    var i = 0
    var initial = Offset(0f, 0f)
    while (i < pathData.length && pathData[i].uppercaseChar() != 'Z') {
        val match = requireNotNull(pattern.matchAt(pathData, i)) {
            "Unsupported expression in $pathData at offset $i"
        }
        val op = match.groupValues[1]
        val x = match.groupValues[2].toFloat()
        val y = match.groupValues[3].toFloat()
        when (op) {
            "M" -> initial = Offset(x, y)
            "L" -> {
                xs += x
                ys += y
            }

            "l" -> {
                xs += (xs.lastOrNull() ?: 0f) + x
                ys += (ys.lastOrNull() ?: 0f) + y
            }

            else -> error("Unsupported operation $op in $pathData at offset $i")
        }
        i += match.value.length
    }
    if (initial.x !in xs) xs += initial.x
    if (initial.y !in ys) ys += initial.y

    val x = xs.sorted()
    val y = ys.sorted()

    check(x.size == y.size)
    check(x.size == 6) { "Path is unsupported, has $x values but must have exactly 6" }

    /*
    A checkmark looks like this, where a, b, c, d, e, f are the corners:
                d
               / \
              /   \
       b     /     e
      / \   /     /
     a   \ /     /
      \   c     /
       \       /
        \     /
         \   /
          \ /
           f
    You can figure out the which are the corners by sorting the x and y coordinates, then the indices will be:
     */
    val a = Offset(x[0], y[3])
    val b = Offset(x[1], y[2])
    val c = Offset(x[2], y[4])
    val d = Offset(x[4], y[0])
    val e = Offset(x[5], y[1])
    val f = Offset(x[3], y[5])

    val start = computeCenter(a, b)
    val turn = computeCenter(c, f)
    val end = computeCenter(d, e)
    println("// strokeWidthPx = (%.2f).dp".format(Locale.ROOT, (b - a).getDistance()))
    println("val startX = %.2ff".format(Locale.ROOT, start.x))
    println("val startY = %.2ff".format(Locale.ROOT, start.y))
    println("val turnX = %.2ff".format(Locale.ROOT, turn.x))
    println("val turnY = %.2ff".format(Locale.ROOT, turn.y))
    println("val endX = %.2ff".format(Locale.ROOT, end.x))
    println("val endY = %.2ff".format(Locale.ROOT, end.y))
}

@Preview
@Composable
private fun CheckboxPreview() {
    PreviewContent {
        Row {
            Column {
                Text(text = "Enabled, Not Checked", typographyToken = OddOneOutTheme.typography.Body.B400)
                Checkbox(rememberCheckboxState(false))
                Text(text = "Enabled, Checked", typographyToken = OddOneOutTheme.typography.Body.B400)
                Checkbox(rememberCheckboxState(true))
            }

            HorizontalSpacerS800()
            Column {
                Text(text = "Disabled, Not Checked", typographyToken = OddOneOutTheme.typography.Body.B400)
                Checkbox(rememberCheckboxState(false), enabled = false)
                Text(text = "Disabled, Checked", typographyToken = OddOneOutTheme.typography.Body.B400)
                Checkbox(rememberCheckboxState(true), enabled = false)
            }
        }
    }
}
