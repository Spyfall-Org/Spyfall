package com.dangerfield.features.joingame.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import com.dangerfield.features.joingame.internal.UnresolvableError.IncompatibleError
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.components.modal.BasicDialog
import com.dangerfield.libraries.ui.preview.PreviewContent
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
        is IncompatibleError -> {
            dictionaryString(R.string.joinGame_versionErrorDialog_header)
        }

        UnresolvableError.UnknownError -> {
            dictionaryString(R.string.joinGame_unknownErrorDialog_header)
        }
    }

    val description = when (unresolvableError) {
        is IncompatibleError -> {
            if (unresolvableError.isCurrentLower) {
                dictionaryString(R.string.joinGame_versionTooLowError_text)
            } else {
                dictionaryString(R.string.joinGame_versionTooHighError_text)
            }
        }

        UnresolvableError.UnknownError -> {
            dictionaryString(R.string.joinGame_unknownError_text)
        }
    }

    val shouldShowUpdateButton =
        unresolvableError is IncompatibleError && unresolvableError.isCurrentLower

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
    PreviewContent {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = IncompatibleError(isCurrentLower = true),
            onUpdateClicked = {}
        )
    }
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogSomeoneElseUpdate() {
    PreviewContent {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = IncompatibleError(isCurrentLower = false),
            onUpdateClicked = {}
        )
    }
}

@Composable
@Preview
private fun PreviewJoinGameErrorDialogUnknown() {
    PreviewContent {
        JoinGameErrorDialog(
            onDismiss = {},
            unresolvableError = UnresolvableError.UnknownError,
            onUpdateClicked = {}
        )
    }
}