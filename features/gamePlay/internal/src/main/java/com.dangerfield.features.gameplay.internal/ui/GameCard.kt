package com.dangerfield.features.gameplay.internal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.components.BadgedBox
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.circleBackground
import com.dangerfield.libraries.ui.thenIf

// TODO cleanup truly this is a mess
// need to make the api much cleaner and maybe even just make some different components
// for different use cases
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    text: String,
    isClickEnabled: Boolean,
    isDisplayingForSelection: Boolean = false,
    onSelectedForVote: () -> Unit = {},
    isSelectedForVote: Boolean = false,
    isFirst: Boolean = false,
) {
    var isMarkedOff by remember { mutableStateOf(false) }
    val radius = Radii.Card

    BadgedBox(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        badge = {
            AnimatedVisibility(visible = !isDisplayingForSelection) {
                FirstBadge(
                    isFirst = isFirst,
                    modifier = Modifier.offset(x = -Dimension.D500, y = Dimension.D100)
                )
            }
        },
        contentRadius = radius,
        content = {
            Surface(
                radius = radius,
                color = OddOneOutTheme.colors.surfacePrimary,
                contentColor = OddOneOutTheme.colors.onSurfacePrimary,
                contentPadding = PaddingValues(Dimension.D800),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .bounceClick {
                        if (!isClickEnabled) return@bounceClick

                        if (isDisplayingForSelection) {
                            onSelectedForVote()
                        } else {
                            isMarkedOff = !isMarkedOff
                        }
                    }
                    .thenIf(isSelectedForVote) {
                        border(
                            width = 2.dp,
                            shape = radius.shape,
                            color = OddOneOutTheme.colors.accent.color
                        )
                    },
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = text,
                        colorResource = if (isMarkedOff) OddOneOutTheme.colors.textDisabled else OddOneOutTheme.colors.text,
                        textDecoration = if (isMarkedOff) TextDecoration.LineThrough else TextDecoration.None,
                    )
                }
            }
        }
    )
}

@Composable
private fun FirstBadge(
    isFirst: Boolean,
    modifier: Modifier = Modifier
) {
    if (isFirst) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                typography = OddOneOutTheme.typography.Label.L700,
                modifier = Modifier.circleBackground(OddOneOutTheme.colors.accent.color, 2.dp),
                text = "1st",
                colorResource = OddOneOutTheme.colors.onAccent
            )
        }
    }
}

@Composable
@Preview
private fun PreviewGameCard() {
    Preview {
        Column(
            modifier = Modifier
                .height(200.dp)
                .width(500.dp)
                .padding(Dimension.D800),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameCard(
                text = "Example",
                isFirst = true,
                isClickEnabled = true
            )
        }
    }
}

@Composable
@Preview
private fun PreviewGameCardSelected() {
    Preview {
        Column(
            modifier = Modifier
                .height(200.dp)
                .width(500.dp)
                .padding(Dimension.D800),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameCard(
                text = "Example",
                isFirst = true,
                isSelectedForVote = true,
                isClickEnabled = true
            )
        }
    }
}

