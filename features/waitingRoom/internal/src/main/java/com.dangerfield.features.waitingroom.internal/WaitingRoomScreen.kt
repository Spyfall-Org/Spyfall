package com.dangerfield.features.waitingroom.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.Radii
import spyfallx.ui.Spacing
import spyfallx.ui.color.background
import spyfallx.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text
import spyfallx.ui.theme.SpyfallTheme

@Composable
fun WaitingRoomScreen(
    modifier: Modifier = Modifier
) {
    val players: List<String> = listOf("Amy", "Justin", "Ryan", "Bryan")

    Screen(
        modifier = modifier,
        header = {
            Header(title = "Waiting for players...")
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(horizontal = Spacing.S1000)) {
            Spacer(modifier = Modifier.height(Spacing.S1000))
            Row {
                Text(text = "Access Code:", typographyToken = SpyfallTheme.typography.Default.Bold)
                Spacer(modifier = Modifier.width(Spacing.S500))
                Text(text = "1234", typographyToken = SpyfallTheme.typography.Default)
            }
            Spacer(modifier = Modifier.height(Spacing.S500))
            LazyColumn {
                itemsIndexed(players) { index, player ->
                    if (index != 0) {
                        Spacer(modifier = Modifier.height(Spacing.S500))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                SpyfallTheme.colorScheme.surfacePrimary,
                                radius = Radii.Card
                            )
                            .padding(horizontal = Spacing.S500)
                            .padding(vertical = Spacing.S400)
                    ) {
                        Text(text = "${index + 1}")
                        Spacer(modifier = Modifier.width(Spacing.S500))
                        Text(text = player)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.S1200))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /*TODO*/ },
                type = ButtonType.Regular
            ) {
                Text(text = "Leave Game")
            }

            Spacer(modifier = Modifier.height(Spacing.S1000))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Start Game")
            }

        }
    }
}

@Composable
@Preview
fun PreviewWaitingRoomScreen() {
    PreviewContent {
        WaitingRoomScreen()
    }
}
