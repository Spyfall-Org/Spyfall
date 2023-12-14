package com.dangerfield.features.waitingroom.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.features.waitingroom.internal.WaitingRoomViewModel.DisplayablePlayer
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.CircularProgressIndicator
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Spacing
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.modifiers.drawVerticalScrollbar
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun WaitingRoomScreen(
    modifier: Modifier = Modifier,
    accessCode: String,
    players: List<DisplayablePlayer>,
    videoCallLink: String?,
    isLoadingRoom: Boolean,
    isLoadingStart: Boolean,
    onStartGameClicked: () -> Unit,
    onCallLinkButtonClicked: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Screen(
        modifier = modifier,
        topBar = {
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
                    OddOneOutTheme.colorScheme.surfaceDisabled.color
                )
                .padding(it)
                .padding(horizontal = Spacing.S1000)
        ) {
            Spacer(modifier = Modifier.height(Spacing.S1000))
            Row {
                Text(text = "Access Code:", typographyToken = OddOneOutTheme.typography.Default.Bold)
                Spacer(modifier = Modifier.width(Spacing.S500))
                Text(
                    modifier = Modifier.weight(1f),
                    text = accessCode,
                    typographyToken = OddOneOutTheme.typography.Default
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

            if (!isLoadingStart) {
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
                    onClick = onStartGameClicked
                ) {
                    Text(text = "Start Game")
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PlayerList(players: List<DisplayablePlayer>) {
    players.forEachIndexed { index, player ->
        if (index != 0) {
            Spacer(modifier = Modifier.height(Spacing.S500))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    OddOneOutTheme.colorScheme.surfacePrimary,
                    radius = Radii.Card
                )
                .padding(horizontal = Spacing.S500)
                .padding(vertical = Spacing.S400)
        ) {
            Text(text = "${index + 1}")
            Spacer(modifier = Modifier.width(Spacing.S500))
            Text(text = player.name)
            if (player.isMe) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(icon = SpyfallIcon.Pencil("Change name"), onClick = { })
            }
        }
    }
}

@Composable
@Preview
fun PreviewWaitingRoomScreen() {
    PreviewContent {
        val players = listOf(
            DisplayablePlayer("Josiah", isMe = false),
            DisplayablePlayer("Arif", isMe = false),
            DisplayablePlayer("Michael", isMe = false),
            DisplayablePlayer("Eli", isMe = true),
            DisplayablePlayer("Nibraas", isMe = false),
            DisplayablePlayer("George", isMe = false)
        )
        WaitingRoomScreen(
            accessCode = "dsv311",
            players = players,
            isLoadingRoom = false,
            isLoadingStart = false,
            videoCallLink = "https://meet.google.com/lookup/abc123",
            onCallLinkButtonClicked = { },
            onStartGameClicked = {}
        )
    }
}
