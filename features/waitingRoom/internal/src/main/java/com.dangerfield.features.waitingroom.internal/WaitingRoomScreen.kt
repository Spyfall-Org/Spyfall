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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import spyfallx.ui.Radii
import spyfallx.ui.Spacing
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.icon.IconButton
import com.dangerfield.libraries.ui.icon.SpyfallIcon
import com.dangerfield.libraries.ui.modifiers.drawVerticalScrollbar
import com.dangerfield.libraries.ui.theme.SpyfallTheme

@Composable
fun WaitingRoomScreen(
    modifier: Modifier = Modifier,
    accessCode: String,
    players: List<String>,
    videoCallLink: String?,
    isLoadingRoom: Boolean,
    isLoadingStart: Boolean,
    onCallLinkButtonClicked: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Screen(
        modifier = modifier,
        header = {
            Header(
                title = "Waiting for players...",
                scrollState = scrollState
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .drawVerticalScrollbar(
                    scrollState,
                    SpyfallTheme.colorScheme.surfaceDisabled.color
                )
                .padding(it)
                .padding(horizontal = Spacing.S1000)
        ) {
            Spacer(modifier = Modifier.height(Spacing.S1000))
            Row {
                Text(text = "Access Code:", typographyToken = SpyfallTheme.typography.Default.Bold)
                Spacer(modifier = Modifier.width(Spacing.S500))
                Text(
                    modifier = Modifier.weight(1f),
                    text = accessCode,
                    typographyToken = SpyfallTheme.typography.Default
                )

                if (videoCallLink != null) {
                    IconButton(
                        icon = SpyfallIcon.VideoCall("Join game video call"),
                        onClick = {
                            onCallLinkButtonClicked(videoCallLink)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.S500))

            if (isLoadingRoom) {
                CircularProgressIndicator()
            } else {
                PlayerList(players)
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
private fun PlayerList(players: List<String>) {
    players.forEachIndexed { index, player ->
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

@Composable
@Preview
fun PreviewWaitingRoomScreen() {
    PreviewContent {
        WaitingRoomScreen(
            accessCode = "dsv311",
            players = listOf("Josiah", "Arif", "Michael", "Eli", "Nibraas", "George"),
            isLoadingRoom = false,
            isLoadingStart = false,
            videoCallLink = "https://meet.google.com/lookup/abc123",
            onCallLinkButtonClicked = { }
        )
    }
}
