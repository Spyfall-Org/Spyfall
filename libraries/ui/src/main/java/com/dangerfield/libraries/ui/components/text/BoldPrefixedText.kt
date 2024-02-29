package com.dangerfield.libraries.ui.components.text

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.makeBold
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.typography.TypographyResource

@Composable
fun BoldPrefixedText(
    modifier: Modifier = Modifier,
    boldText: String,
    regularText: String,
    textAlign: TextAlign = TextAlign.Start,
    typography: TypographyResource = OddOneOutTheme.typography.Body.B800
) {
    val string = "$boldText $regularText".makeBold(boldText)

    Row(modifier = modifier) {
       Text(
           textAlign = textAlign,
           text = string,
           typographyToken = typography
       )
    }
}

@Preview
@Composable
fun BoldPrefixedTextPreview() {
    Preview {
        BoldPrefixedText(
            boldText = "Role: ",
            regularText = "Spy",
            typography = OddOneOutTheme.typography.Body.B800
        )
    }
}