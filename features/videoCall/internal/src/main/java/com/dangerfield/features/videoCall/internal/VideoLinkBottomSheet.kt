package com.dangerfield.features.videoCall.internal

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.ScrollingColumnWithFadingEdge
import com.dangerfield.libraries.ui.preview.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.preview.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS800
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.modal.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.modal.bottomsheet.iconTopAccessory
import com.dangerfield.libraries.ui.components.modal.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.getBoldUnderlinedSpan
import com.dangerfield.oddoneoout.features.videocall.internal.R
import kotlinx.coroutines.delay

@Composable
@Suppress("MaxLineLength")
fun VideoLinkBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    link: String,
    onDismiss: (BottomSheetState) -> Unit
) {
    val chooserTitle = dictionaryString(R.string.video_openWith_header)
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(link)) }
    val chooser = remember { Intent.createChooser(intent, chooserTitle) }
    val canOpenVideoLink = remember { intent.resolveActivity(context.packageManager) != null }
    var showLinkCopiedCheckmark by remember { mutableStateOf(false) }

    LaunchedEffect(showLinkCopiedCheckmark) {
        if (showLinkCopiedCheckmark) {
            delay(2000)
            showLinkCopiedCheckmark = false
        }
    }

    BasicBottomSheet(
        onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        showCloseButton = true,
        topAccessory = iconTopAccessory(icon = SpyfallIcon.VideoCall(null)),
        modifier = modifier,
        topContent = {
            Text(text = dictionaryString(R.string.videoCall_detailDialog_title))
        },
        content = {
            ScrollingColumnWithFadingEdge {
                val annotatedString = getBoldUnderlinedSpan(
                    fullString = dictionaryString(R.string.videoLink_detailDialogDescription_text),
                    boldString = dictionaryString(R.string.videoLink_beCareful_label)
                )

                Text(text = annotatedString)

                VerticalSpacerS800()

                Row(
                    verticalAlignment = CenterVertically,
                ) {
                    
                    if (showLinkCopiedCheckmark) {
                        IconButton(icon = SpyfallIcon.Check(null), onClick = { })
                    } else {
                        IconButton(
                            size = IconButton.Size.Small,
                            icon = SpyfallIcon.Copy(dictionaryString(R.string.videoLink_copyLink_a11y)),
                            onClick = {
                                clipboardManager.setText(AnnotatedString(link))
                            }
                        )
                    }

                    HorizontalSpacerS600()

                    SelectionContainer {
                        Text(text = link)
                    }
                }
            }
        },
        bottomContent = {
            if (canOpenVideoLink) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.S800),
                    onClick = {
                        onDismiss(bottomSheetState)
                        context.startActivity(chooser)
                    }
                ) {
                    Text(text = dictionaryString(R.string.app_open_action))
                }
            }
        }
    )
}

@Composable
@ThemePreviews
private fun VideoLinkBottomSheetPreview() {
    PreviewContent {
        val sheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
        VideoLinkBottomSheet(
            bottomSheetState = sheetState,
            link = "https://www.google.com",
            onDismiss = { }
        )
    }
}