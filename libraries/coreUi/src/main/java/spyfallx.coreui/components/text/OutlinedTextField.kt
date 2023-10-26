package spyfallx.coreui.components.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spyfallx.coreui.PreviewContent
import spyfallx.coreui.Radii
import spyfallx.coreui.theme.SpyfallTheme

@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextConfig.current.typographyToken?.style ?: SpyfallTheme.typography.Body.B500.style,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    cursorBrush: Brush = SolidColor(Color.Black)
) {

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                isError = isError,
                label = label,
                placeholder = {
                    ProvideTextConfig(config = TextConfig(
                        typographyToken = SpyfallTheme.typography.Body.B600,
                        color = SpyfallTheme.colorScheme.textDisabled,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Start,
                        maxLines = 1,

                    )) {
                        placeholder?.invoke()
                    }
                },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                colors = outlinedTextFieldColors,
                contentPadding = outlineTextFieldPadding,
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = outlinedTextFieldColors,
                        shape = Radii.Card.shape,
                        focusedBorderThickness = FocusedBorderThickness,
                        unfocusedBorderThickness = UnfocusedBorderThickness,
                    )
                },
            )
        }
    )
}

private val outlineTextFieldPadding
    @Composable
    get() = OutlinedTextFieldDefaults.contentPadding()

private val outlinedTextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SpyfallTheme.colorScheme.background.color,
        unfocusedContainerColor = SpyfallTheme.colorScheme.background.color,
        disabledContainerColor = SpyfallTheme.colorScheme.background.color,
        focusedBorderColor = SpyfallTheme.colorScheme.border.color,
        unfocusedBorderColor = SpyfallTheme.colorScheme.border.color,
    )

private val FocusedBorderThickness = 2.dp
private val UnfocusedBorderThickness = 2.dp

@Composable
@Preview
private fun PreviewOutlinedTextField() {
    PreviewContent(modifier = Modifier.padding(24.dp)) {
        OutlinedTextField(value = "Hello World", onValueChange = { })
    }
}

@Composable
@Preview
private fun PreviewOutlinedTextFieldEmpty() {
    PreviewContent(modifier = Modifier.padding(24.dp)) {
        OutlinedTextField(value = "", onValueChange = { }, placeholder = { Text("Type something")})
    }
}
