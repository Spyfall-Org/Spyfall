package com.dangerfield.features.createPack.internal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.HorizontalSpacerD600
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.Switch
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun ConfirmDetailsScreen(
    modifier: Modifier = Modifier,
    isPublic: Boolean,
    onIsPublicChanged: (Boolean) -> Unit,
    onDone: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val scrollState = rememberScrollState()

    BackHandler {
        onNavigateBack()
    }

    Screen(
        modifier = modifier,
        topBar = {
            Header(
                navigationIcon = SpyfallIcon.ArrowBack("Close"),
                onNavigateBack = onNavigateBack,
                title = "Step 3 of 3",
                titleAlignment = Alignment.CenterHorizontally,
                typographyToken = OddOneOutTheme.typography.Label.L800
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = Dimension.D1000),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            VerticalSpacerD1200()

            Text(text = "Share the fun?")

            VerticalSpacerD1200()

            Row(modifier = Modifier.fillMaxWidth()) {
                Switch(checked = isPublic, onCheckedChange = onIsPublicChanged)
                HorizontalSpacerD600()
                Text(text = "Make Public")
            }

            VerticalSpacerD800()

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "If public, this pack will be discoverable by other users",
                typography = OddOneOutTheme.typography.Label.L600
            )

            VerticalSpacerD1200()

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
                style = ButtonStyle.Background,
            ) {
                Text(text = "Done")
            }

            VerticalSpacerD1200()
        }
    }
}

@Preview
@Composable
fun PreviewConfirmDetailsScreen() {
    Preview() {
        ConfirmDetailsScreen(
           onNavigateBack = {},
            isPublic = true,
            onIsPublicChanged = { },
            onDone = { -> }
        )
    }
}
