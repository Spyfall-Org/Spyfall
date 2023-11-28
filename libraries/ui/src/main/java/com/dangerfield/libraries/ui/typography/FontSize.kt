package spyfallx.ui.typography

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import spyfallx.ui.NumericalValues
import spyfallx.ui.Spacing
import spyfallx.ui.asSp
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import kotlin.math.roundToInt
import kotlin.reflect.KProperty0

@Suppress("MagicNumber")
object FontSize {
    /** 10dp */
    val S400 = NumericalValues.V400.asSp()

    /** 12dp */
    val S500 = NumericalValues.V500.asSp()

    /** 14dp */
    val S600 = NumericalValues.V600.asSp()

    /** 16dp */
    val S700 = NumericalValues.V700.asSp()

    /** 20dp */
    val S800 = NumericalValues.V800.asSp()

    /** 24dp */
    val S900 = NumericalValues.V900.asSp()

    /** 28dp */
    val S1000 = NumericalValues.V1000.asSp()

    /** 34dp */
    val S1100 = NumericalValues.V1100.asSp()

    /** 40dp */
    val S1200 = NumericalValues.V1200.asSp()

    /** 48dp */
    val S1300 = NumericalValues.V1300.asSp()

    /** 58dp */
    val S1400 = NumericalValues.V1400.asSp()

    /** 70dp */
    val S1500 = NumericalValues.V1500.asSp()

    /** 84dp */
    val S1600 = NumericalValues.V1600.asSp()
}

@Suppress("MagicNumber")
@Preview(device = "spec:id=reference_phone,shape=Normal,width=1600,height=2000,unit=dp,dpi=200")
@Composable
private fun FontSizePreview() {
    PreviewContent(
        contentPadding = PaddingValues(Spacing.S500),
        showBackground = true
    ) {
        val sizes = listOf(
            NumericalValues::V100 to null,
            NumericalValues::V200 to null,
            NumericalValues::V300 to null,
            NumericalValues::V400 to FontSize.S400,
            NumericalValues::V500 to FontSize.S500,
            NumericalValues::V600 to FontSize.S600,
            NumericalValues::V700 to FontSize.S700,
            NumericalValues::V800 to FontSize.S800,
            NumericalValues::V900 to FontSize.S900,
            NumericalValues::V1000 to FontSize.S1000,
            NumericalValues::V1100 to FontSize.S1100,
            NumericalValues::V1200 to FontSize.S1200,
            NumericalValues::V1300 to FontSize.S1300,
            NumericalValues::V1400 to FontSize.S1400,
            NumericalValues::V1500 to FontSize.S1500,
            NumericalValues::V1600 to FontSize.S1600
        ).reversed()
        Column {
            PreviewRow(
                token = {
                    Text("Token", style = SpyfallTheme.typography.Body.B600.style, color = ColorPrimitive.Black800.color)
                },
                value = {
                    Text("SP Value", style = SpyfallTheme.typography.Body.B600.style, color = ColorPrimitive.Black600.color)
                },
                poppins = {
                    Text("Poppins Example", style = SpyfallTheme.typography.Body.B600.style, color = ColorPrimitive.Black600.color)
                },
            )
            for ((value, size) in sizes) {
                Divider(Modifier.fillMaxWidth(), color = ColorPrimitive.Black400.color)
                PreviewRow(
                    value,
                    hasSize = size != null
                )
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun PreviewRow(
    value: KProperty0<Dp>,
    hasSize: Boolean,
) {
    val color = (if (hasSize) ColorPrimitive.Black800 else ColorPrimitive.Black500).color
    PreviewRow(
        token = {
            Text(
                text = "font-size-${value.name.removePrefix("value")}",
                style = SpyfallTheme.typography.Heading.H600.style,
                color = color
            )
        },
        value = {
            Text(
                text = "${value.get().value.roundToInt()}sp",
                style = SpyfallTheme.typography.Body.B700.style,
                color = color
            )
        },
        poppins = {
            Text(
                text = "Enter your name",
                fontSize = value.get().value.sp,
                fontFamily = com.dangerfield.libraries.ui.typography.FontFamilyToken.Poppins.fontFamily,
                color = color,
                softWrap = false,
                maxLines = 1
            )
        }
    )
}

@Suppress("MagicNumber")
@Composable
private fun PreviewRow(
    token: @Composable () -> Unit,
    value: @Composable () -> Unit,
    poppins: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = Spacing.S500),
        horizontalArrangement = Arrangement.spacedBy(Spacing.S700),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1 / 6f)) { token() }
        Box(Modifier.weight(1 / 6f)) { value() }
        Box(Modifier.weight(2 / 6f)) { poppins() }
    }
}
