package spyfallx.coreui.color

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
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
import spyfallx.coreui.thenIf
import java.util.*

enum class ColorPrimitive(val color: Color, val colorOn: Color) {
    Unspecified(Color.Unspecified, Color.Unspecified),

    Black900(Color(0xFF000000), Color(0xFFFFFFFF)),
    Black800(Color(0xFF1A1A1A), Color(0xFFFFFFFF)),
    Black700(Color(0xFF313131), Color(0xFFFFFFFF)),
    Black600(Color(0xFF767676), Color(0xFFFFFFFF)),
    Black500(Color(0xFFAAAAAA), Color(0xFF1A1A1A)),
    Black400(Color(0xFFCCCCCC), Color(0xFF1A1A1A)),
    Black300(Color(0xFFEEEEEE), Color(0xFF1A1A1A)),
    Black200(Color(0xFFF6F6F6), Color(0xFF1A1A1A)),
    Black100(Color(0xFFFBFBFB), Color(0xFF1A1A1A)),
    White900(Color(0xFFFFFFFF), Color(0xFF1A1A1A)),

    GrapeJelly500(Color(0xFF9533C7), Color(0xFFFFFFFF)),
    SkyDive400(Color(0xFF00A0EF), Color(0xFF1A1A1A)),
    MintyFresh300(Color(0xFF2FD566), Color(0xFF1A1A1A)),
    TangerineTwist600(Color(0xFFFF5800), Color(0xFFFFFFFF)),
    CherryPop700(Color(0xFFE3212F), Color(0xFFFFFFFF)),
}

val ColorPrimitive.designSystemName: String
    get() = name.lowercase()
        .replace(Regex("""([A-Za-z]+)(\d+)"""), """color-$1-$2""")

enum class ColorGradientPrimitive(val from: Color, val to: Color) {
    Dark(Color.Black, ColorPrimitive.Black800.color),
    Light(ColorPrimitive.Black100.color, ColorPrimitive.Black200.color);

    val colorOn: Color = ColorPrimitive.values()
        .first { it.color == from || it.color == to }
        .colorOn
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
    val color = try {
        ColorPrimitive.valueOf("$family$weight")
    } catch (ignored: IllegalArgumentException) {
        null
    }
    val shape = RoundedCornerShape(4.dp)
    val border = Modifier.border(1.dp, ColorPrimitive.Black400.color, shape)
    val background = if (color == null) {
        border
    } else {
        Modifier
            .background(color.color, shape)
            .thenIf(color.color.luminance() > 0.8f) { border }
    }

    Column(
        modifier = modifier
            .aspectRatio(144f / 190f)
            .then(background)
            .padding(12.dp)
    ) {
        val textStyle = TextStyle(
            fontSize = 14.sp,
            color = color?.colorOn ?: ColorPrimitive.Black500.color
        )
        if (color == null) {
            Text(
                text = "X",
                style = textStyle.copy(fontSize = 48.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "DNE",
                style = textStyle.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Text(
                text = color.designSystemName,
                style = textStyle.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = color.color.toHexString(),
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
                    0f to gradientPrimitive.from,
                    0.5f to gradientPrimitive.from,
                    1f to gradientPrimitive.to
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
        Text(gradientPrimitive.from.toHexString(), style = textStyle)
        Spacer(Modifier.height(8.dp))
        Text("Color2", style = textStyle)
        Text(gradientPrimitive.to.toHexString(), style = textStyle)
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
