package spyfallx.coreui.color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.icon.LargeIcon
import spyfallx.coreui.icon.SmallIcon
import spyfallx.coreui.icon.SpyfallIcon
import spyfallx.coreui.thenIf
import java.util.Locale

@Suppress("MagicNumber")
sealed class ColorPrimitive(val color: Color) {
    data object Unspecified: ColorPrimitive(Color.Unspecified)
    data object Black900: ColorPrimitive(Color(0xFF000000))
    data object Black800: ColorPrimitive(Color(0xFF1A1A1A))
    data object Black700: ColorPrimitive(Color(0xFF313131))
    data object Black600: ColorPrimitive(Color(0xFF767676))
    data object Black500: ColorPrimitive(Color(0xFFAAAAAA))
    data object Black400: ColorPrimitive(Color(0xFFCCCCCC))
    data object Black300: ColorPrimitive(Color(0xFFEEEEEE))
    data object Black200: ColorPrimitive(Color(0xFFF6F6F6))
    data object Black100: ColorPrimitive(Color(0xFFFBFBFB))
    data object White900: ColorPrimitive(Color(0xFFFFFFFF))

    data object GrapeJelly500: ColorPrimitive(Color(0xFF9533C7))
    data object SkyDive400: ColorPrimitive(Color(0xFF00A0EF))
    data object MintyFresh300: ColorPrimitive(Color(0xFF2FD566))
    data object TangerineTwist600: ColorPrimitive(Color(0xFFFF5800))
    data object CherryPop700: ColorPrimitive(Color(0xFFE3212F))
    class FromColor(color: Color): ColorPrimitive(color)

    val onColorPrimitive: ColorPrimitive
        get() {
            return if (color.luminance() > 0.4) Black900 else White900
        }

}

@Suppress("ComplexMethod")
fun getColorPrimitive(family: String, weight: Int): ColorPrimitive? {
    return when (family) {
        "Black" -> when (weight) {
            900 -> ColorPrimitive.Black900
            800 -> ColorPrimitive.Black800
            700 -> ColorPrimitive.Black700
            600 -> ColorPrimitive.Black600
            500 -> ColorPrimitive.Black500
            400 -> ColorPrimitive.Black400
            300 -> ColorPrimitive.Black300
            200 -> ColorPrimitive.Black200
            100 -> ColorPrimitive.Black100
            else -> null
        }
        "White" -> when (weight) {
            900 -> ColorPrimitive.White900
            else -> null
        }
        "GrapeJelly" -> when (weight) {
            500 -> ColorPrimitive.GrapeJelly500
            else -> null
        }
        "SkyDive" -> when (weight) {
            400 -> ColorPrimitive.SkyDive400
            else -> null
        }
        "MintyFresh" -> when (weight) {
            300 -> ColorPrimitive.MintyFresh300
            else -> null
        }
        "TangerineTwist" -> when (weight) {
            600 -> ColorPrimitive.TangerineTwist600
            else -> null
        }
        "CherryPop" -> when (weight) {
            700 -> ColorPrimitive.CherryPop700
            else -> null
        }
        else -> null
    }
}

val ColorPrimitive.designSystemName: String
    get() = this.toString().lowercase()
        .replace(Regex("""([A-Za-z]+)(\d+)"""), """color-$1-$2""")

enum class ColorGradientPrimitive(val from: ColorPrimitive, val to: ColorPrimitive) {
    Dark(ColorPrimitive.FromColor(Color.Black), ColorPrimitive.Black800),
    Light(ColorPrimitive.Black100, ColorPrimitive.Black200);

    val colorOn: Color = from.onColorPrimitive.color
}

val ColorGradientPrimitive.designSystemName: String
    get() = "color-gradient-${name.lowercase()}"

@Preview(device = "spec:shape=Normal,width=1680,height=2838,unit=dp,dpi=100")
@Composable
private fun ColorPrimitivesPreview() {
    val weights = (900 downTo 100 step 100).toList()
    val families = listOf("Black", "White", "GrapeJelly", "SkyDive", "MintyFresh", "TangerineTwist", "CherryPop")
    val columnCount = weights.size + 1
    PreviewContent(showBackground = true) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.wrapContentHeight()
        ) {
            item {
                PreviewHeaderItem("Color Family")
            }
            weights.fastForEach { weight ->
                item {
                    PreviewHeaderItem("$weight", modifier = Modifier.padding(start = 6.dp))
                }
            }

            item(span = { GridItemSpan(columnCount) }) { DividerRow() }

            families.fastForEach { family ->
                item {
                    PreviewRowTitle(family)
                }
                weights.fastForEach { weight ->
                    item {
                        ColorCard(family, weight, modifier = Modifier.padding(6.dp))
                    }
                }
                item(span = { GridItemSpan(columnCount) }) { DividerRow() }
            }
            item {
                PreviewRowTitle("Gradients", modifier = Modifier.padding(6.dp))
            }
            val spanCount = weights.size / ColorGradientPrimitive.values().size
            for (gradient in ColorGradientPrimitive.values()) {
                item(span = { GridItemSpan(spanCount) }) {
                    GradientCard(gradient, modifier = Modifier.padding(6.dp))
                }
            }
        }
    }
}

@Composable
private fun DividerRow() {
    Divider(
        color = ColorPrimitive.Black400.color,
        modifier = Modifier.padding(vertical = 24.dp)
    )
}

@Composable
private fun ColorCard(
    family: String,
    weight: Int,
    modifier: Modifier = Modifier,
) {

    val colorPrimitive = getColorPrimitive(family, weight)
    val shape = RoundedCornerShape(4.dp)
    val border = Modifier.border(1.dp, ColorPrimitive.Black400.color, shape)
    val background = if (colorPrimitive == null) {
        border
    } else {
        Modifier
            .background(colorPrimitive.color, shape)
            .thenIf(colorPrimitive.color.luminance() > 0.8f) { border }
    }
    
    Column(
        modifier = modifier
            .aspectRatio(144f / 190f)
            .then(background)
            .padding(12.dp)
    ) {
        val textStyle = TextStyle(
            fontSize = 14.sp,
            color = colorPrimitive?.onColorPrimitive?.color ?: ColorPrimitive.Black500.color
        )
        if (colorPrimitive == null) {
            LargeIcon(imageVector = SpyfallIcon.Close.imageVector, contentDescription = null)
            Text(
                text = "Does not Exist",
                style = textStyle.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Text(
                text = colorPrimitive.designSystemName,
                style = textStyle.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = colorPrimitive.color.toHexString(),
                style = textStyle.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
@Composable
private fun GradientCard(
    gradientPrimitive: ColorGradientPrimitive,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(
                brush = Brush.verticalGradient(
                    0f to gradientPrimitive.from.color,
                    0.5f to gradientPrimitive.from.color,
                    1f to gradientPrimitive.to.color
                ),
                shape = shape
            )
            .border(1.dp, ColorPrimitive.Black400.color, shape)
            .padding(12.dp)
    ) {
        val textStyle = TextStyle(
            fontSize = 14.sp,
            color = gradientPrimitive.colorOn,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = gradientPrimitive.designSystemName,
            style = textStyle.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.weight(1f))
        Text("Color1", style = textStyle)
        Text(gradientPrimitive.from.color.toHexString(), style = textStyle)
        Spacer(Modifier.height(8.dp))
        Text("Color2", style = textStyle)
        Text(gradientPrimitive.to.color.toHexString(), style = textStyle)
    }
}

@Composable
private fun PreviewHeaderItem(
    text: String,
    modifier: Modifier = Modifier,
    color: ColorPrimitive = ColorPrimitive.Black800,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 14.sp,
        color = color.color
    )
}

@Composable
private fun PreviewRowTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = 20.sp,
        color = ColorPrimitive.Black800.color,
        fontWeight = FontWeight.Bold,
        modifier = modifier.height(68.dp)
    )
}

fun Color.toHexString() =
    if (alpha == 1f) "#%06X".format(Locale.ROOT, toArgb() and 0x00FFFFFF)
    else "#%08X".format(Locale.ROOT, toArgb())
