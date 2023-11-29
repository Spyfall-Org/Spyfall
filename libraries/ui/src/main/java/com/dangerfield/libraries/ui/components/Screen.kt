package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = SpyfallTheme.colorScheme.background.color,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = header,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        Box(
            Modifier.fillMaxSize(),
            propagateMinConstraints = true) {
            content(paddingValues)
        }
    }
}
