package com.dangerfield.features.gameplay.internal

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Spacing
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import spyfallx.ui.then
import spyfallx.ui.thenIf

@Composable
fun GamePlayGrid(
    items: List<String>
) {
    NonLazyVerticalGrid(columns = 2, data = items) { index, item ->
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
