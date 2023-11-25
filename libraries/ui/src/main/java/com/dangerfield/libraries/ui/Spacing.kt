package spyfallx.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.color.ColorPrimitive
import spyfallx.ui.theme.SpyfallTheme
import kotlin.math.roundToInt

object Spacing {
    /** 2dp */
    val S50 = NumericalValues.V50

    /** 4dp */
    val S100 = NumericalValues.V100

    /** 6dp */
    val S200 = NumericalValues.V200

    /** 8dp */
    val S300 = NumericalValues.V300

    /** 10dp */
    val S400 = NumericalValues.V400

    /** 12dp */
    val S500 = NumericalValues.V500

    /** 14dp */
    val S600 = NumericalValues.V600

    /** 16dp */
    val S700 = NumericalValues.V700

    /** 20dp */
    val S800 = NumericalValues.V800

    /** 24dp */
    val S900 = NumericalValues.V900

    /** 28dp */
    val S1000 = NumericalValues.V1000

    /** 34dp */
    val S1100 = NumericalValues.V1100

    /** 40dp */
    val S1200 = NumericalValues.V1200

    /** 48dp */
    val S1300 = NumericalValues.V1300

    /** 58dp */
    val S1400 = NumericalValues.V1400

    /** 70dp */
    val S1500 = NumericalValues.V1500

    /** 84dp */
    val S1600 = NumericalValues.V1600
}

/**
 * A margin that should be applied to the left and right of the screen.
 *
 * Sometimes a value less than this should be used because this is the optical margin. So when using things like icons
 * or other components with internal padding you sometimes need a smaller margin.
 */
val Spacing.HorizontalScreenMargin get() = S800

@Preview(device = "spec:id=reference_phone,shape=Normal,width=500,height=2000,unit=dp,dpi=100")
@Composable
private fun SpacingPreview() {
    PreviewContent(showBackground = true) {
        CompositionLocalProvider(LocalContentColor provides ColorPrimitive.Black800.color) {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.S500)
                    .fillMaxWidth()
            ) {
                val spacers = listOf(
                    Spacing::S50,
                    Spacing::S100,
                    Spacing::S200,
                    Spacing::S300,
                    Spacing::S400,
                    Spacing::S500,
                    Spacing::S600,
                    Spacing::S700,
                    Spacing::S800,
                    Spacing::S900,
                    Spacing::S1000,
                    Spacing::S1100,
                    Spacing::S1200,
                    Spacing::S1300,
                    Spacing::S1400,
                    Spacing::S1500,
                    Spacing::S1600
                ).reversed()
                ProvideTextStyle(
                    TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                ) {
                    SpacingPreviewRow(
                        start = { Text("Token", style = SpyfallTheme.typography.Body.B600.style) },
                        center = { Text("Value", style = SpyfallTheme.typography.Body.B600.style) },
                        end = {}
                    )
                }

                val widestSpacer = spacers.maxOf { it() }
                for (spacer in spacers) {
                    Divider(
                        color = ColorPrimitive.Black400.color
                    )
                    SpacingPreviewRow(
                        start = {
                            Text(
                                text = spacer.name.replace("spacer", "spacer-"),
                                style = SpyfallTheme.typography.Heading.H800.style
                            )
                        },
                        center = {
                            Text("${spacer().value.roundToInt()}dp", style = SpyfallTheme.typography.Heading.H800.style)
                        },
                        end = {
                            Box(
                                modifier = Modifier.width(widestSpacer),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(spacer())
                                        .background(ColorPrimitive.CherryPop700.color)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpacingPreviewRow(
    start: @Composable () -> Unit,
    center: @Composable () -> Unit,
    end: @Composable () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .weight(1f)
                .padding(
                    horizontal = Spacing.S500
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            start()
        }
        Box(
            Modifier
                .weight(1f)
                .padding(
                    horizontal = Spacing.S500
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            center()
        }
        Box(
            Modifier
                .weight(1f)
                .padding(
                    vertical = Spacing.S900,
                    horizontal = Spacing.S500
                ),
            contentAlignment = Alignment.CenterEnd
        ) {
            end()
        }
    }
}
