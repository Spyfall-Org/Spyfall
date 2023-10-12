package spyfallx.coreui.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.sizeIfEmpty(size: Dp): Modifier = sizeIfEmpty(size, size)
fun Modifier.widthIfEmpty(width: Dp): Modifier = sizeIfEmpty(width, 0.dp)
fun Modifier.heightIfEmpty(height: Dp): Modifier = sizeIfEmpty(0.dp, height)

/**
 * Returns a [Modifier] that makes the target have the given [width] and [height] if it has zero bounds.
 *
 * This is useful in custom layouts when you want an optional composable to have a size if it's not provided, similar
 * to gone margin in ConstraintLayout. This way you can unconditionally add the size and it will only be applied if
 * the target has zero bounds.
 */
fun Modifier.sizeIfEmpty(width: Dp, height: Dp): Modifier =
    then(
        EmptyPaddingModifier(
            whenEmpty = PaddingValues(start = width, top = height),
            inspectorInfo = debugInspectorInfo {
                name = "sizeIfEmpty"
                properties["width"] = width
                properties["height"] = height
            }
        )
    )

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PaddingIfEmptyPreview() {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            Modifier
                .background(Color.Red)
                .sizeIfEmpty(10.dp)
                .background(Color.Blue)
        )
        Box(
            Modifier
                .background(Color.Red)
                .sizeIfEmpty(10.dp)
                .background(Color.Blue)
                .size(50.dp)
        )
    }
}
