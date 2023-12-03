package com.dangerfield.libraries.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.ui.Spacing
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.reflect.KProperty0

@Suppress("MagicNumber")
internal object NumericalValues {
    /** 2dp */
    val V50 = 2.dp

    /** 4dp */
    val V100 = 4.dp

    /** 6dp */
    val V200 = 6.dp

    /** 8dp */
    val V300 = 8.dp

    /** 10dp */
    val V400 = 10.dp

    /** 12dp */
    val V500 = 12.dp

    /** 14dp */
    val V600 = 14.dp

    /** 16dp */
    val V700 = 16.dp

    /** 20dp */
    val V800 = 20.dp

    /** 24dp */
    val V900 = 24.dp

    /** 28dp */
    val V1000 = 28.dp

    /** 34dp */
    val V1100 = 34.dp

    /** 40dp */
    val V1200 = 40.dp

    /** 48dp */
    val V1300 = 48.dp

    /** 58dp */
    val V1400 = 58.dp

    /** 70dp */
    val V1500 = 70.dp

    /** 84dp */
    val V1600 = 84.dp

    fun validate(value: Dp) {
        getValue(value)
    }

    @Suppress("ComplexMethod")
    internal fun getValue(dimension: Dp): Int =
        when (dimension) {
            V50 -> 50
            V100 -> 100
            V200 -> 200
            V300 -> 300
            V400 -> 400
            V500 -> 500
            V600 -> 600
            V700 -> 700
            V800 -> 800
            V900 -> 900
            V1000 -> 1000
            V1100 -> 1100
            V1200 -> 1200
            V1300 -> 1300
            V1400 -> 1400
            V1500 -> 1500
            V1600 -> 1600
            else -> throw IllegalArgumentException("$dimension is not a valid dimension.")
        }
}

internal fun Dp.asSp(): TextUnit = value.sp

@Preview(device = "spec:id=reference_phone,shape=Normal,width=1200,height=2000,unit=dp,dpi=200")
@Composable
private fun NumericalValuesPreview() {
    PreviewContent(
        contentPadding = PaddingValues(Spacing.S900),
        showBackground = true
    ) {
        val values = listOf(
            NumericalValues::V50,
            NumericalValues::V100,
            NumericalValues::V200,
            NumericalValues::V300,
            NumericalValues::V400,
            NumericalValues::V500,
            NumericalValues::V600,
            NumericalValues::V700,
            NumericalValues::V800,
            NumericalValues::V900,
            NumericalValues::V1000,
            NumericalValues::V1100,
            NumericalValues::V1200,
            NumericalValues::V1300,
            NumericalValues::V1400,
            NumericalValues::V1500,
            NumericalValues::V1600
        )

        val boxAlignmentLines = values.associateBy(
            { it },
            { HorizontalAlignmentLine(::max) }
        )

        val textAlignmentLines = values.associateBy(
            { it },
            { HorizontalAlignmentLine(::max) }
        )

        Layout(
            content = {
                NumericalValuesPreviewContent(values, boxAlignmentLines, textAlignmentLines)
                for (value in values) {
                    BoxToTextLine()
                }
            }
        ) { measurables, constraints ->
            val content = measurables.first().measure(constraints)
            val margin = Spacing.S900.roundToPx()
            val boxRight = content[BoxRight] + margin
            val textLeft = content[TextLeft] - margin
            val linesWidth = textLeft - boxRight
            val lines = measurables.drop(1).mapIndexed { index, measurable ->
                val value = values[index]
                val boxCenter = content[boxAlignmentLines.getValue(value)]
                val textCenter = content[textAlignmentLines.getValue(value)]
                measurable.measure(Constraints.fixed(linesWidth, (boxCenter - textCenter).absoluteValue)) to min(
                    boxCenter,
                    textCenter
                )
            }

            layout(content.width, content.height) {
                content.place(0, 0)
                for ((line, top) in lines) {
                    line.place(boxRight, top)
                }
            }
        }
    }
}

@Composable
private fun NumericalValuesPreviewContent(
    values: List<KProperty0<Dp>>,
    boxAlignmentLines: Map<KProperty0<Dp>, HorizontalAlignmentLine>,
    textAlignmentLines: Map<KProperty0<Dp>, HorizontalAlignmentLine>,
) {
    Row(verticalAlignment = Alignment.Bottom) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            var sum = 0.dp
            for (value in values) {
                val size = value()
                val alignmentLine = boxAlignmentLines.getValue(value)
                NumericalValueBox(size, sum, alignmentLine)
                sum += size
            }
        }
        Spacer(Modifier.width(300.dp))
        Column {
            Row {
                Text(
                    text = "System value",
                    style = SpyfallTheme.typography.Body.B600.style,
                    color = ColorPrimitive.Black800.color,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "DP value",
                    style = SpyfallTheme.typography.Body.B600.style,
                    color = ColorPrimitive.Black600.color,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(Spacing.S500))
            for (value in values.reversed()) {
                val alignmentLine = textAlignmentLines.getValue(value)
                Divider(Modifier.fillMaxWidth(), color = SpyfallTheme.colorScheme.border.color)
                NumericalValueText(value, alignmentLine)
            }
        }
    }
}

@Composable
private fun NumericalValueText(
    value: KProperty0<Dp>,
    alignmentLine: HorizontalAlignmentLine,
) {
    Row(
        modifier = Modifier
            .padding(vertical = Spacing.S700)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(
                    width = placeable.width,
                    height = placeable.height,
                    alignmentLines = mapOf(
                        alignmentLine to placeable.height / 2,
                        TextLeft to 0
                    )
                ) {
                    placeable.place(0, 0)
                }
            }
    ) {
        Text(
            text = value.name.removePrefix("value"),
            style = SpyfallTheme.typography.Heading.H800.style,
            color = ColorPrimitive.Black800.color,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${value.get().value.roundToInt()}dp",
            style = SpyfallTheme.typography.Heading.H800.style,
            color = ColorPrimitive.Black600.color,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NumericalValueBox(
    size: Dp,
    previousBoxes: Dp,
    alignmentLine: HorizontalAlignmentLine,
) {
    Box(
        Modifier
            .width(size + previousBoxes)
            .aspectRatio(1f)
            .background(ColorPrimitive.CherryPop700.color.copy(alpha = 0.1f))
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(
                    width = placeable.width,
                    height = placeable.height,
                    alignmentLines = mapOf(
                        alignmentLine to (size / 2).roundToPx(),
                        BoxRight to placeable.width
                    )
                ) {
                    placeable.place(0, 0)
                }
            }
    )
}

@Composable
private fun BoxToTextLine() {
    Box(
        Modifier.drawWithCache {
            val segmentWidth = Spacing.S900.toPx()
            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(segmentWidth, size.height)
                lineTo(size.width - segmentWidth, 0f)
                lineTo(size.width, 0f)
            }
            onDrawBehind {
                drawPath(path, color = ColorPrimitive.Black800.color, style = Stroke(width = 1.dp.toPx()))
            }
        }
    )
}

private val BoxRight = VerticalAlignmentLine(::max)
private val TextLeft = VerticalAlignmentLine(::min)
