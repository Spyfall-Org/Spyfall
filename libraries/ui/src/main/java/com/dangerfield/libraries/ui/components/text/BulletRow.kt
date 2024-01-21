package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dangerfield.libraries.ui.HorizontalSpacerS800

@Composable
fun BulletRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { }
) {
    Row(modifier = modifier) {
        Text(text = "•")
        HorizontalSpacerS800()
        content()
    }
}