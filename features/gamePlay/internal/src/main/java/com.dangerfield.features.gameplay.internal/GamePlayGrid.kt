package com.dangerfield.features.gameplay.internal

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Spacing
import spyfallx.coreui.components.Grid
import spyfallx.coreui.then
import spyfallx.coreui.thenIf

@Composable
fun GamePlayGrid(
    items: List<String>
) {
    Grid(columns = 2, items = items.size) { index ->
        val text = items[index]
        val shouldPadEnd = index % 2 == 0
        val shouldPadTop = index > 1
        GameCard(
            text = text,
            modifier = Modifier
                .thenIf(shouldPadTop) {
                    padding(top = Spacing.S500)
                }
                .then {
                    if (shouldPadEnd) {
                        padding(end = Spacing.S200)
                    } else {
                        padding(start = Spacing.S200)
                    }
                }
        )
    }
}


@Composable
@Preview
private fun PreviewCrossOutGrid() {
    PreviewContent {
        GamePlayGrid(
            listOf("one", "two", "three", "four", "five", "six", "seven")
        )
    }
}