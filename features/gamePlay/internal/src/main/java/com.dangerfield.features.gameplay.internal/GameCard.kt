package com.dangerfield.features.gameplay.internal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Radii
import spyfallx.coreui.Spacing
import spyfallx.coreui.circleBackground
import spyfallx.coreui.color.background
import spyfallx.coreui.components.text.Text
import spyfallx.coreui.theme.NoRippleTheme
import spyfallx.coreui.theme.SpyfallTheme

@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    text: String,
    isFirst: Boolean = false,
) {
    var isMarkedOff by remember { mutableStateOf(false) }
    Box {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    SpyfallTheme.colorScheme.surfacePrimary,
                    radius = Radii.Card
                )
                .clickable { isMarkedOff = !isMarkedOff }
                .padding(Spacing.S500),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                textDecoration = if (isMarkedOff) TextDecoration.LineThrough else TextDecoration.None
            )
        }

        if (isFirst) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.circleBackground(SpyfallTheme.colorScheme.accent.color, 2.dp),
                    text = "1st",
                    color = SpyfallTheme.colorScheme.onAccent
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewGameCard() {
    PreviewContent {
        GameCard(
            text = "Example",
            isFirst = true
        )
    }
}
