package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.Preview
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.components.text.Text

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.0.dp,
    color: Color = ColorResource.Black500.color,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
@Preview
fun PreviewDivider() {
    Preview {
        Column {
            Text(text = "Text")
            Spacer(modifier = Modifier.height(Dimension.D500))
            com.dangerfield.libraries.ui.components.HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimension.D500))
            Text(text = "Text")
        }
    }
}