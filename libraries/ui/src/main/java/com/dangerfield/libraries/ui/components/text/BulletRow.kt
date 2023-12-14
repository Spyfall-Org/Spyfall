package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.dangerfield.libraries.ui.HorizontalSpacerS800

@Composable
fun BulletRow(
    content: @Composable () -> Unit = { }
) {
    Row {
        Text(text = "•")
        HorizontalSpacerS800()
        content()
    }
}