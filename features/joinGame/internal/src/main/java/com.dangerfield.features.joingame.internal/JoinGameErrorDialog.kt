package com.dangerfield.features.joingame.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import com.dangerfield.features.joingame.internal.UnresolvableError.IncompatibleGameVersionError
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.oddoneoout.features.joingame.internal.R

@Composable
fun JoinGameErrorDialog(
    onDismiss: () -> Unit,
    onUpdateClicked: () -> Unit,
    unresolvableError: UnresolvableError
) {

    PageLogEffect(
        route = route("join_game_error"),
        type = PageType.Dialog,
        extras = bundleOf(
            "error_type" to unresolvableError.javaClass.simpleName
        )
    )

    val title = when (unresolvableError) {
        is IncompatibleGameVersionError -> {
            dictionaryString(R.string.joinGame_versionErrorDialog_header)
        }

        UnresolvableError.UnknownError -> {
            dictionaryString(R.string.joinGame_unknownErrorDialog_header)
        }

        UnresolvableError.CouldNotFetchPacksNeededError -> {
            dictionaryString(R.string.joinGame_couldNotFetchPacksNeededError_header)
        }
    }

    val description = when (unresolvableError) {
        is IncompatibleGameVersionError -> {
            if (unresolvableError.isCurrentLower) {
                dictionaryString(R.string.joinGame_versionTooLowError_text)
            } else {
                dictionaryString(R.string.joinGame_versionTooHighError_text)
            }
        }

        UnresolvableError.UnknownError -> {
            dictionaryString(R.string.joinGame_unknownError_text)
        }

        UnresolvableError.CouldNotFetchPacksNeededError -> {
            dictionaryString(R.string.joinGame_couldNotFetchPacksNeededError_text)
        }
    }

    val shouldShowUpdateButton =
        unresolvableError is IncompatibleGameVersionError && unresolvableError.isCurrentLower

    BasicDialog(
        onDismissRequest = onDismiss,
        title = title,
        description = description,
        primaryButtonText = if (shouldShowUpdateButton) {
            dictionaryString(R.string.joinGame_update_action)
        } else {
            dictionaryString(id = R.string.app_okay_action)
        },
        onPrimaryButtonClicked = {
            onDismiss()
            if (shouldShowUpdateButton) {
                onUpdateClicked()
            }
        }
    )
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogUpdate() {
    Preview {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = IncompatibleGameVersionError(isCurrentLower = true),
            onUpdateClicked = {}
        )
    }
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogSomeoneElseUpdate() {
    Preview {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = IncompatibleGameVersionError(isCurrentLower = false),
            onUpdateClicked = {}
        )
    }
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogUnknown() {
    Preview {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = UnresolvableError.UnknownError,
            onUpdateClicked = {}
        )
    }
}


@Composable
@Preview
private fun PreviewJoinGameErrorDialogPacksVersion() {
    Preview {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = UnresolvableError.CouldNotFetchPacksNeededError,
            onUpdateClicked = {}
        )
    }
}