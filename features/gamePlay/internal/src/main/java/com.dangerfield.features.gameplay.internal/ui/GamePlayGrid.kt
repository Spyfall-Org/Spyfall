package com.dangerfield.features.gameplay.internal.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import spyfallx.ui.then
import spyfallx.ui.thenIf

@Composable
fun GamePlayGrid(
    indexOfFirst: Int? = null,
    items: List<String>,
    isDisplayingForSelection: Boolean,
    isClickEnabled: Boolean,
    onItemSelectedForVote: (Int?) -> Unit = {}
) {
    var indexSelectedForVote by remember { mutableStateOf<Int?>(null) }

    NonLazyVerticalGrid(columns = 2, data = items) { index, item ->
        val shouldPadEnd = index % 2 == 0
        val shouldPadTop = index > 1
        GameCard(
            text = item,
            isFirst = indexOfFirst == index,
            isDisplayingForSelection = isDisplayingForSelection,
            onSelectedForVote = {
                val alreadySelected = indexSelectedForVote == index
                val indexUpdate = if (alreadySelected) null else index
                indexSelectedForVote = indexUpdate
                onItemSelectedForVote(indexUpdate)
            } ,
            isSelectedForVote = index  == indexSelectedForVote,
            isClickEnabled = isClickEnabled,
            modifier = Modifier
                .fillMaxHeight()
                .padding(Spacing.S50)
                .thenIf(shouldPadTop) {
                    padding(top = Spacing.S50)
                }
                .then {
                    if (shouldPadEnd) {
                        padding(end = Spacing.S200)
                    } else {
                        padding(start = Spacing.S200)
                    }
                },
        )
    }
}


@Composable
@Preview
private fun PreviewCrossOutGridVoting() {
    PreviewContent {
        GamePlayGrid(
            indexOfFirst = 1,
            isDisplayingForSelection = true,
            isClickEnabled = true,
            items = listOf("one", "two", "three", "four", "five but longer than normal", "six", "seven")
        )
    }
}

@Composable
@Preview
private fun PreviewCrossOutGridNotVoting() {
    PreviewContent {
        GamePlayGrid(
            indexOfFirst = 1,
            isDisplayingForSelection = false,
            isClickEnabled = true,
            items = listOf("one", "two", "three", "four", "five but longer than normal", "six", "seven")
        )
    }
}
