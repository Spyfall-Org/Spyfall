package com.dangerfield.libraries.ui.components.icon

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.NumericalValues.V1300
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.NumericalValues.V1000
import com.dangerfield.libraries.ui.NumericalValues.V1200
import com.dangerfield.libraries.ui.NumericalValues.V400
import com.dangerfield.libraries.ui.NumericalValues.V800
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun Icon(
    spyfallIcon: SpyfallIcon,
    modifier: Modifier = Modifier,
    iconSize: IconSize = IconSize.Small,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = spyfallIcon.imageVector,
        contentDescription = spyfallIcon.contentDescription,
        modifier = modifier.size(iconSize.dp),
        tint = tint
    )
}

@Composable
fun SmallIcon(
    spyfallIcon: SpyfallIcon,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = spyfallIcon.imageVector,
        contentDescription = spyfallIcon.contentDescription,
        modifier = modifier.size(IconSize.Small.dp),
        tint = tint
    )
}

@Composable
fun MediumIcon(
    spyfallIcon: SpyfallIcon,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = spyfallIcon.imageVector,
        contentDescription = spyfallIcon.contentDescription,
        modifier = modifier.size(IconSize.Medium.dp),
        tint = tint
    )
}

@Composable
fun LargeIcon(
    spyfallIcon: SpyfallIcon,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = spyfallIcon.imageVector,
        contentDescription = spyfallIcon.contentDescription,
        modifier = modifier.size(IconSize.Large.dp),
        tint = tint
    )
}

enum class IconSize(val dp: Dp) {
    Smallest(V400),
    Small(V800),
    Medium(V1000),
    Large(V1200),
    Largest(V1300),
}

@Preview(device = "spec:id=reference_phone,shape=Normal,width=1000,height=600,unit=dp,dpi=500")
@Composable
private fun IconPreview() {
    PreviewContent(showBackground = true) {
        LazyColumn {
            items(IconSize.values()) {
                Icon(
                    iconSize = it,
                    spyfallIcon = SpyfallIcon.Check("check")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${it.name} Icon", typographyToken = OddOneOutTheme.typography.Body.B500
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}
