package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Spacing
import androidx.compose.material3.Divider as MaterialDivider

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.0.dp,
    color: Color = ColorPrimitive.Black500.color,
) {
    MaterialDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
@ThemePreviews
fun PreviewDivider() {
    PreviewContent {
        Column {
            Text(text = "Text")
            Spacer(modifier = Modifier.height(Spacing.S500))
            Divider()
            Spacer(modifier = Modifier.height(Spacing.S500))
            Text(text = "Text")
        }
    }
}