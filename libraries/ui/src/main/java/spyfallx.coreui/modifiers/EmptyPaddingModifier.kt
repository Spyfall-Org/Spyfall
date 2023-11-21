package spyfallx.ui.modifiers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import spyfallx.ui.calculateHorizontalPadding
import spyfallx.ui.calculateVerticalPadding

internal class EmptyPaddingModifier(
    val whenEmpty: PaddingValues = NoPadding,
    val whenNonEmpty: PaddingValues = NoPadding,
    inspectorInfo: InspectorInfo.() -> Unit,
) : LayoutModifier, InspectorValueInfo(inspectorInfo) {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(
            constraints.offset(
                horizontal = -whenNonEmpty.calculateHorizontalPadding().roundToPx(),
                vertical = -whenNonEmpty.calculateVerticalPadding().roundToPx()
            )
        )
        val padding = if (placeable.width == 0 && placeable.height == 0) {
            whenEmpty
        } else {
            whenNonEmpty
        }
        val leftPadding = padding.calculateLeftPadding(layoutDirection).roundToPx()
        val rightPadding = padding.calculateRightPadding(layoutDirection).roundToPx()
        val topPadding = padding.calculateTopPadding().roundToPx()
        val bottomPadding = padding.calculateBottomPadding().roundToPx()
        require(leftPadding >= 0 && topPadding >= 0 && rightPadding >= 0 && bottomPadding >= 0) {
            "Padding must be non-negative"
        }

        return layout(
            constraints.constrainWidth(placeable.width + (leftPadding + rightPadding)),
            constraints.constrainHeight(placeable.height + (topPadding + bottomPadding))
        ) {
            placeable.place(leftPadding, topPadding)
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || other is EmptyPaddingModifier &&
                whenEmpty == other.whenEmpty &&
                whenNonEmpty == other.whenNonEmpty

    override fun hashCode(): Int = 31 * whenEmpty.hashCode() + whenNonEmpty.hashCode()
}

private val NoPadding = PaddingValues(0.dp)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun EmptyPaddingModifierPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                        .paddingIfNonEmpty(10.dp)
                        .background(Color.Blue)
                )
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                        .paddingIfNonEmpty(10.dp)
                        .background(Color.Blue)
                        .size(50.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                        .sizeIfEmpty(20.dp)
                        .background(Color.Blue)
                )
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                        .sizeIfEmpty(20.dp)
                        .background(Color.Blue)
                        .size(50.dp)
                )
            }
        }
    }
}
