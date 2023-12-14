package com.dangerfield.libraries.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.dangerfield.libraries.ui.theme.OddOneOutTheme


fun Context.themedComposeView(
    viewCompositionStrategy: ViewCompositionStrategy = ViewCompositionStrategy.Dynamic,
    content: @Composable () -> Unit,
) = ComposeView(this).apply {
    setViewCompositionStrategy(viewCompositionStrategy)
    setContent {
        OddOneOutTheme {
            content()
        }
    }
}

fun ComposeView.themedComposeContent(
    viewCompositionStrategy: ViewCompositionStrategy = ViewCompositionStrategy.Dynamic,
    content: @Composable () -> Unit,
) = this.apply {
    setViewCompositionStrategy(viewCompositionStrategy)
    setContent {
        OddOneOutTheme {
            content()
        }
    }
}
