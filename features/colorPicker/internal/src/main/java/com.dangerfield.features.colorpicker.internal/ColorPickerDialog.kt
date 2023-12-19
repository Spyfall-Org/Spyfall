@file:Suppress("MagicNumber")

package com.dangerfield.features.colorpicker.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Sizes
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS1200
import com.dangerfield.libraries.ui.clip
import com.dangerfield.libraries.ui.color.ColorPrimitive
import com.dangerfield.libraries.ui.components.NonLazyVerticalGrid
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.color.ThemeColor
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.spyfall.libraries.resources.R

@Composable
fun ColorPickerDialog(
    onConfigSelected: (ColorConfig) -> Unit,
    selectedConfig: ColorConfig,
    onDismiss: () -> Unit,
    colorConfigs: List<ColorConfig>,
    modifier: Modifier = Modifier
) {

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_animation))

    BasicDialog(
        topContent = {
            Text(text = "Theme")
        },
        content = {
            Column {
                NonLazyVerticalGrid(
                    columns = 3,
                    data = colorConfigs,
                    verticalSpacing = Spacing.S500,
                    horizontalSpacing = Spacing.S500,
                ) { index, item ->

                    val color = (item as? ColorConfig.Specific)?.color?.colorPrimitive?.color
                        ?: OddOneOutTheme.colorScheme.onBackground.color

                    var isSelected by rememberSaveable { mutableStateOf(selectedConfig == item) }

                    LaunchedEffect(selectedConfig) {
                        isSelected = selectedConfig == item
                    }

                    Box(
                        modifier = Modifier
                            .height(Sizes.S1500)
                            .aspectRatio(1f)
                            .clip(Radii.Card)
                            .background(color)
                            .clickable { onConfigSelected(item) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (item is ColorConfig.Random) {
                            Text(
                                text = "Random",
                                typographyToken = OddOneOutTheme.typography.Body.B400.Bold,
                                color = OddOneOutTheme.colorScheme.background,
                            )
                        }

                        if (isSelected) {
                            Box {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(ColorPrimitive.Black900.color.copy(alpha = 0.8f))
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
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier,
        bottomContent = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Okay")
            }
        },
    )
}

@Composable
@ThemePreviews
fun PreviewColorPickerDialog() {
    PreviewContent {

        val colorConfigs = ThemeColor.entries.map { ColorConfig.Specific(it) } + ColorConfig.Random

        ColorPickerDialog(
            onConfigSelected = {},
            selectedConfig = ColorConfig.Specific(ThemeColor.CherryPop700),
            onDismiss = {},
            colorConfigs = colorConfigs
        )
    }
}
