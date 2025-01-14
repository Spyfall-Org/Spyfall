package com.dangerfield.features.forcedupdate.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.forcedupdate.internal.R

@Composable
fun ForcedUpdateScreen(
    onOpenAppStoreClicked: () -> Unit
) {
    Screen { paddingValues ->
        Column(
            Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.10f))
            Text(
                text = dictionaryString(R.string.forcedUpdate_update_header),
                typography = OddOneOutTheme.typography.Display.D1000,
                modifier = Modifier.padding(horizontal = Dimension.D500),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimension.D500))

            Text(
                text = dictionaryString(R.string.forcedUpdate_message_text),
                typography = OddOneOutTheme.typography.Body.B700,
                modifier = Modifier.padding(horizontal = Dimension.D500),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = Dimension.D1100),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onOpenAppStoreClicked,
                    modifier = Modifier.fillMaxWidth(),
                    type = ButtonType.Primary
                ) {
                    Text(text = dictionaryString(R.string.forcedUpdate_openStore_action))
                }

                Spacer(modifier = Modifier.height(Dimension.D1000))

            }
            Spacer(modifier = Modifier.height(Dimension.D1000))
        }
    }
}

@Composable
@Preview
private fun PreviewForcedUpdateScreen() {
    Preview {
        ForcedUpdateScreen(
            onOpenAppStoreClicked = {},
        )
    }
}

