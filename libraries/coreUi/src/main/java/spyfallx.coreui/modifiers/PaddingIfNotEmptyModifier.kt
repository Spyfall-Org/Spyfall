package spyfallx.coreui.modifiers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.paddingIfNonEmpty(all: Dp): Modifier = paddingIfNonEmpty(PaddingValues(all))

fun Modifier.paddingIfNonEmpty(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): Modifier = paddingIfNonEmpty(PaddingValues(start = start, top = top, end = end, bottom = bottom))

fun Modifier.paddingIfNonEmpty(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
): Modifier = paddingIfNonEmpty(PaddingValues(horizontal = horizontal, vertical = vertical))

/**
 * Returns a [Modifier] that adds the specified padding only if the target has non zero bounds.
 *
 * This is useful in custom layouts when you want an optional composable to have padding only if it's provided. This
 * was you can unconditionally add the padding and it will only be applied if the target has non zero bounds.
 */
fun Modifier.paddingIfNonEmpty(paddingValues: PaddingValues): Modifier =
    then(
        EmptyPaddingModifier(
            whenNonEmpty = paddingValues,
            inspectorInfo = debugInspectorInfo {
                name = "paddingIfNonEmpty"
                value = paddingValues
            }
        )
    )

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PaddingIfNonEmptyPreview() {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.background(Color.Red).paddingIfNonEmpty(10.dp).background(Color.Blue))
        Box(Modifier.background(Color.Red).paddingIfNonEmpty(10.dp).background(Color.Blue).size(50.dp))
    }
}
