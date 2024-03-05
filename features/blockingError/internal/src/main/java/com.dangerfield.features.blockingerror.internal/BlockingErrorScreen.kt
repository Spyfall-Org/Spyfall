package com.dangerfield.features.blockingerror.internal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.blockingerror.internal.R

// TODO feature, I should probably have some check to see how many times
// in a row this pops up and in those cases clear all app state.
@Composable
fun BlockingErrorScreen(
    onBackPressed: () -> Unit = {}
) {
    Screen { paddingValues ->

        BackHandler(onBack = onBackPressed)

        Column(
            Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.10f))
            Text(
                text = dictionaryString(R.string.blockingError_screen_header),
                typography = OddOneOutTheme.typography.Display.D1000,
                modifier = Modifier.padding(horizontal = Dimension.D500),
                textAlign = TextAlign.Center
            )
            Text(
                text = dictionaryString(R.string.blockingError_error_body),
                typography = OddOneOutTheme.typography.Body.B700,
                modifier = Modifier.padding(horizontal = Dimension.D500),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
@Preview
private fun PreviewBlockingErrorScreen() {
    Preview {
        BlockingErrorScreen()
    }
}
