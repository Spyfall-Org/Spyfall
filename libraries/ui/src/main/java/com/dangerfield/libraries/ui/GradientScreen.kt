package com.dangerfield.libraries.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun GradientScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = OddOneOutTheme.colorScheme.background.color,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val accentColor = OddOneOutTheme.colorScheme.accent.color

    Screen(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        Box{
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundToAccentGradient(accentColor)
            )

            content(paddingValues)
        }
    }
}

fun Modifier.backgroundToAccentGradient(accentColor: Color) = this.background(
    brush = Brush.linearGradient(
        0f to accentColor,
        0.25f to ColorPrimitive.Purple900.color,
        1f to ColorPrimitive.Purple900.color,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
)

@Preview
@Composable
fun GradientScreenPreview() {
    PreviewContent {
        GradientScreen(content = {
            Box(modifier = Modifier.fillMaxSize()) {

            }
        })
    }
}
