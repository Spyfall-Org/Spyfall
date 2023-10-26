@file:Suppress("MagicNumber")
package com.dangerfield.features.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Radii
import spyfallx.coreui.Spacing
import spyfallx.coreui.color.AccentColor
import spyfallx.coreui.color.ColorPrimitive
import spyfallx.coreui.color.background
import spyfallx.coreui.color.border
import spyfallx.coreui.components.text.Text
import spyfallx.coreui.theme.SpyfallTheme
import spyfallx.coreui.thenIf

@Composable
fun ColorPickerDialog(
    onColorSelected: (ColorPrimitive) -> Unit,
    selectedColor: ColorPrimitive,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(SpyfallTheme.colorScheme.backgroundOverlay)) {
        Dialog(onDismissRequest = onDismiss) {

            Column(
                modifier
                    .background(
                        color = SpyfallTheme.colorScheme.background.color,
                        shape = Radii.Card.shape
                    )
                    .padding(Spacing.S500)

            ) {
                Text(text = "Set App Color")
                Spacer(modifier = Modifier.height(Spacing.S100))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    content = {
                        items(AccentColor.values()) { color ->
                            Box(
                                modifier = Modifier
                                    .padding(Spacing.S1100)
                                    .height(50.dp)
                                    .width(50.dp)
                                    .background(color = color.colorPrimitive.color, shape = Radii.Card.shape)
                                    .thenIf(color.colorPrimitive == selectedColor) {
                                        border(
                                            radius = Radii.Card,
                                            color = SpyfallTheme.colorScheme.onSurfacePrimary,
                                            width = 3.dp
                                        )
                                    }
                                    .padding(Spacing.S1100)
                                    .clickable { onColorSelected(color.colorPrimitive) }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewColorPickerDialog() {
    PreviewContent {
        ColorPickerDialog(
            onColorSelected = {},
            selectedColor = ColorPrimitive.CherryPop700,
            onDismiss = {}
        )
    }
}
