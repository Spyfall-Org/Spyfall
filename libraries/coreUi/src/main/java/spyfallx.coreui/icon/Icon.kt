package spyfallx.coreui.icon

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import spyfallx.coreui.NumericalValues.V1100
import spyfallx.coreui.NumericalValues.V1300
import spyfallx.coreui.NumericalValues.V300
import spyfallx.coreui.NumericalValues.V700
import spyfallx.coreui.NumericalValues.V900
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.components.text.Text


enum class IconSize(val dp: Dp) {
    Smallest(V300),
    Small(V700),
    Medium(V900),
    Large(V1100),
    Largest(V1300),
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: IconSize = IconSize.Small,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(iconSize.dp),
        tint = tint
    )
}

@Composable
fun SmallIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(IconSize.Small.dp),
        tint = tint
    )
}

@Composable
fun MediumIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(IconSize.Medium.dp),
        tint = tint
    )
}

@Composable
fun LargeIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(IconSize.Large.dp),
        tint = tint
    )
}

@Preview(device = "spec:id=reference_phone,shape=Normal,width=1000,height=600,unit=dp,dpi=500")
@Composable
private fun IconPreview() {
    PreviewContent(showBackground = true) {
        LazyColumn {
            items(IconSize.values()) {
                Icon(
                    iconSize = it,
                    imageVector = SpyfallIcon.Check.imageVector,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${it.name} Icon")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}