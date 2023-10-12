package spyfallx.coreui.components.header

import androidx.compose.foundation.layout.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.components.text.Text
import spyfallx.coreui.icon.LargeIcon
import spyfallx.coreui.icon.SpyfallIcon
import spyfallx.coreui.theme.SpyfallTheme
import androidx.compose.material3.TopAppBar as MaterialTopAppBar

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SpyfallTheme.colorScheme.surfacePrimary.color,
    navigationIcon: SpyfallIcon,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    MaterialTopAppBar(
        title = {
            Text(text = title, typographyToken = SpyfallTheme.typography.Heading.H1000)
        },
        modifier = modifier,
        navigationIcon = {
            LargeIcon(imageVector = navigationIcon.imageVector, contentDescription = "")
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
            navigationIcon = SpyfallIcon.ArrowBack
        )
    }
}

