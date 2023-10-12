package spyfallx.coreui.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.LayoutDirection

@Suppress("ComplexMethod")
fun Modifier.alignmentLines(
    left: AlignmentLine? = null,
    start: AlignmentLine? = null,
    top: AlignmentLine? = null,
    bottom: AlignmentLine? = null,
    right: AlignmentLine? = null,
    end: AlignmentLine? = null,
    centerX: AlignmentLine? = null,
    centerY: AlignmentLine? = null,
): Modifier {
    require(
        left != null ||
                start != null ||
                top != null ||
                bottom != null ||
                right != null ||
                end != null ||
                centerX != null ||
                centerY != null
    ) {
        "No alignment lines specified"
    }
    return layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val alignmentLines = buildMap {
            left?.let { put(it, 0) }
            start?.let {
                put(
                    it,
                    when (layoutDirection) {
                        LayoutDirection.Ltr -> 0
                        LayoutDirection.Rtl -> placeable.width
                    }
                )
            }
            top?.let { put(it, 0) }
            right?.let { put(it, placeable.width) }
            end?.let {
                put(
                    it,
                    when (layoutDirection) {
                        LayoutDirection.Ltr -> placeable.width
                        LayoutDirection.Rtl -> 0
                    }
                )
            }
            bottom?.let { put(it, placeable.height) }
            centerX?.let { put(it, placeable.width / 2) }
            centerY?.let { put(it, placeable.height / 2) }
        }
        layout(placeable.width, placeable.height, alignmentLines) {
            placeable.place(0, 0)
        }
    }
}
