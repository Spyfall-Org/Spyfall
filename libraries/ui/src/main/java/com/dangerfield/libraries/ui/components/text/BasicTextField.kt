package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import spyfallx.ui.typography.TypographyToken

@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    typographyToken: TypographyToken = LocalTextConfig.current.typographyToken
        ?: SpyfallTheme.typography.Default,
    color: Color = SpyfallTheme.colorScheme.onBackground.color,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    cursorBrush: Brush = SolidColor(SpyfallTheme.colorScheme.accent.color),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
) {
    val textSelectionColors = TextSelectionColors(
        handleColor = SpyfallTheme.colorScheme.accent.color,
        backgroundColor = SpyfallTheme.colorScheme.accent.color.copy(alpha = 0.4F)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        SelectionContainer {
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = typographyToken.style(color),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                visualTransformation = visualTransformation,
                onTextLayout = onTextLayout,
                interactionSource = interactionSource,
                cursorBrush = cursorBrush,
                decorationBox = decorationBox
            )

        }
    }
}

@Composable
@Preview
private fun PreviewBasicTextField() {
    PreviewContent(modifier = Modifier.padding(24.dp)) {
        BasicTextField(value = "Hello World", onValueChange = { })
    }
}
