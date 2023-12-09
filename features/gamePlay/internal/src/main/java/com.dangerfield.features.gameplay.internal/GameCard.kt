package com.dangerfield.features.gameplay.internal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.components.BadgedBox
import com.dangerfield.libraries.ui.components.Surface
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import com.dangerfield.libraries.ui.Spacing
import spyfallx.ui.circleBackground

@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    text: String,
    isFirst: Boolean = false,
) {
    var isMarkedOff by remember { mutableStateOf(false) }

    val radius = Radii.Card

    BadgedBox(
        modifier = modifier,
        badge = {
            FirstBadge(
                isFirst = isFirst,
                modifier = Modifier.offset(x = -Spacing.S500, y = Spacing.S100)
            )
        },
        contentRadius = radius,
        content = {
            Surface(
                radius = radius,
                color = SpyfallTheme.colorScheme.surfacePrimary,
                contentColor = SpyfallTheme.colorScheme.onSurfacePrimary,
                contentPadding = PaddingValues(Spacing.S800),
                modifier = Modifier.clickable { isMarkedOff = !isMarkedOff }
            ) {
                Text(
                    text = text,
                    color = if (isMarkedOff) SpyfallTheme.colorScheme.textDisabled else SpyfallTheme.colorScheme.text,
                    textDecoration = if (isMarkedOff) TextDecoration.LineThrough else TextDecoration.None,
                )
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
                typographyToken = SpyfallTheme.typography.Label.L700,
                modifier = Modifier.circleBackground(SpyfallTheme.colorScheme.accent.color, 2.dp),
                text = "1st",
                color = SpyfallTheme.colorScheme.onAccent
            )
        }
    }
}

@Composable
@ThemePreviews
private fun PreviewGameCard() {
    PreviewContent {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameCard(
                text = "Example",
                isFirst = true
            )
        }
    }
}
