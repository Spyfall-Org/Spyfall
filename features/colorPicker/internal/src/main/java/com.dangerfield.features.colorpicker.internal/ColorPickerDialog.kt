@file:Suppress("MagicNumber")

package com.dangerfield.features.colorpicker.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.clip
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

import com.dangerfield.oddoneoout.features.colorpicker.internal.R

@Composable
fun ColorPickerDialog(
    onColorConfigSelected: (ColorConfig) -> Unit,
    selectedColorConfig: ColorConfig,
    onDismiss: () -> Unit,
    colorConfigs: List<ColorConfig>,
    modifier: Modifier = Modifier
) {

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_animation))

    BasicDialog(
        topContent = {
            Text(text = dictionaryString(R.string.colorPicker_theme_header))
        },
        content = {
            Column {
                Text(
                    text = dictionaryString(R.string.colorPicker_color_header),
                    typographyToken = OddOneOutTheme.typography.Heading.H800
                )

                VerticalSpacerD500()

                NonLazyVerticalGrid(
                    columns = 3,
                    data = colorConfigs,
                    verticalSpacing = Dimension.D500,
                    horizontalSpacing = Dimension.D500,
                ) { index, item ->

                    val color = (item as? ColorConfig.Specific)?.color?.colorResource?.color
                        ?: OddOneOutTheme.colors.onBackground.color

                    var isSelected by rememberSaveable { mutableStateOf(selectedColorConfig == item) }

                    LaunchedEffect(selectedColorConfig) {
                        isSelected = selectedColorConfig == item
                    }

                    Box(
                        modifier = Modifier
                            .bounceClick { onColorConfigSelected(item) }
                            .height(Dimension.D1500)
                            .aspectRatio(1f)
                            .clip(Radii.Card)
                            .background(color),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (item is ColorConfig.Random) {
                            Text(
                                text = dictionaryString(R.string.colorPicker_random_label),
                                typographyToken = OddOneOutTheme.typography.Body.B400.Bold,
                                colorResource = OddOneOutTheme.colors.background,
                            )
                        }

                        if (isSelected) {
                            Box {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(ColorResource.Black900.color.copy(alpha = 0.8f))
                                )
                                LottieAnimation(
                                    modifier = Modifier.fillMaxSize(),
                                    composition = lottieComposition,
                                    isPlaying = isSelected,
                                    speed = 1.5f
                                )
                            }
                        }
                    }
                }

                VerticalSpacerD1200()
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier,
        bottomContent = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = dictionaryString(id = R.string.app_okay_action))
            }
        },
    )
}


@Composable
@Preview
fun PreviewColorPickerDialog() {
    Preview {

        val colorConfigs = ThemeColor.entries.map { ColorConfig.Specific(it) } + ColorConfig.Random

        ColorPickerDialog(
            onColorConfigSelected = {},
            selectedColorConfig = ColorConfig.Specific(ThemeColor.CherryPop700),
            onDismiss = {},
            colorConfigs = colorConfigs,
        )
    }
}
