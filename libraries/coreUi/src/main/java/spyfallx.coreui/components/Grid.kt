package spyfallx.coreui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Grid(
    columns: Int,
    items: Int,
    modifier: Modifier = Modifier,
    layoutItem: @Composable (Int) -> Unit,
) {

    val rows = (items / columns) + if (items % columns != 0) 1 else 0

    Column(modifier) {
        for (rowIndex in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (colIndex in 0 until columns) {
                    val itemIndex = rowIndex * columns + colIndex
                    if (itemIndex < items) {
                        Box(modifier = Modifier.weight(1f)) {
                            layoutItem(itemIndex)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f)) // Empty space for missing items
                    }
                }
            }
        }
    }
}