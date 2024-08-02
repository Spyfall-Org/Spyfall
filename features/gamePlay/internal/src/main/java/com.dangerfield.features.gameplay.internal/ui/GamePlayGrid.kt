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
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.then
import com.dangerfield.libraries.ui.thenIf

@Composable
fun GamePlayGrid(
    indexOfFirst: Int? = null,
    items: List<String>,
    isDisplayingForSelection: Boolean,
    isClickEnabled: Boolean,
    selectedItem: String? = null,
    onItemSelectedForVote: (Int?) -> Unit = {}
) {
    var indexSelectedForVote by remember {
        mutableStateOf<Int?>(
            items.indexOf(selectedItem).takeIf { it != -1 }
        )
    }

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
            },
            isSelectedForVote = index == indexSelectedForVote,
            isClickEnabled = isClickEnabled,
            modifier = Modifier
                .bounceClick()
                .fillMaxHeight()
                .padding(Dimension.D50)
                .thenIf(shouldPadTop) {
                    padding(top = Dimension.D50)
                }
                .then {
                    if (shouldPadEnd) {
                        padding(end = Dimension.D200)
                    } else {
                        padding(start = Dimension.D200)
                    }
                },
        )
    }
}


@Composable
@Preview
private fun PreviewCrossOutGridVoting() {
    Preview {
        GamePlayGrid(
            indexOfFirst = 1,
            isDisplayingForSelection = true,
            isClickEnabled = true,
            items = listOf(
                "one",
                "two",
                "three",
                "four",
                "five but longer than normal",
                "six",
                "seven"
            )
        )
    }
}

@Composable
@Preview
private fun PreviewCrossOutGridNotVoting() {
    Preview {
        GamePlayGrid(
            indexOfFirst = 1,
            isDisplayingForSelection = false,
            isClickEnabled = true,
            items = listOf(
                "one",
                "two",
                "three",
                "four",
                "five but longer than normal",
                "six",
                "seven"
            )
        )
    }
}
