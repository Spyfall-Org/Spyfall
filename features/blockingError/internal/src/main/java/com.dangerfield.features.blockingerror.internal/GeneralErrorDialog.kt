package com.dangerfield.features.blockingerror.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.components.dialog.BasicDialog
import com.dangerfield.oddoneoout.libraries.dictionary.R

@Composable
fun GeneralErrorDialog(
    onDismiss: () -> Unit,
    errorClass: String?
) {

    PageLogEffect(
        route = route("general_error_dialog"),
        type = PageType.Dialog,
        extras = bundleOf(
            "error_class" to (errorClass ?: "unknown")
        )
    )

    BasicDialog(
        onDismissRequest = onDismiss,
        title = dictionaryString(R.string.generalError_title_text),
        description = dictionaryString(R.string.generalError_description_text),
        primaryButtonText = dictionaryString(R.string.app_okay_action),
        onPrimaryButtonClicked = onDismiss
    )
}


@Composable
@Preview
private fun PreviewJoinGameErrorDialogUnknown() {
    Preview {
        GeneralErrorDialog(
            onDismiss = {},
            errorClass = null
        )
    }
}