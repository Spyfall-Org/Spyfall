package com.dangerfield.libraries.ui.components.header

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.text.Text
import spyfallx.ui.theme.SpyfallTheme
import spyfallx.ui.typography.TypographyToken
import androidx.compose.material3.TopAppBar as MaterialTopAppBar

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    typographyToken: TypographyToken = SpyfallTheme.typography.Heading.H1100,
    backgroundColor: Color = SpyfallTheme.colorScheme.background.color,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    MaterialTopAppBar(
        title = {
            Text(text = title, typographyToken = typographyToken)
        },
        modifier = modifier,
        navigationIcon = {
        },
        actions = actions,
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            scrolledContainerColor = backgroundColor,
            navigationIconContentColor = SpyfallTheme.colorScheme.onBackground.color,
            titleContentColor = SpyfallTheme.colorScheme.onBackground.color,
            actionIconContentColor = SpyfallTheme.colorScheme.onBackground.color
        )
    )
}


@Preview
@Composable
private fun PreviewHeader() {
    PreviewContent {
        Header(
            title = "Heading Title",
        )
    }
}

