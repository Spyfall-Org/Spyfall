package com.dangerfield.features.videoCall.internal

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.dictionary.dictionaryString
import com.dangerfield.libraries.ui.HorizontalSpacerD600
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.addStyle
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.addClickableUrl
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.oddoneoout.features.videocall.internal.R
import kotlinx.coroutines.delay

@Composable
@Suppress("MaxLineLength")
fun VideoLinkBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    link: String,
    onVideoLinkClicked: (BottomSheetState) -> Unit = {},
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
        modifier = modifier,
        stickyTopContent = {
            Text(text = dictionaryString(R.string.videoCall_detailDialog_title))
        },
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                val styledString = dictionaryString(R.string.videoLink_detailDialogDescription_text).addStyle(
                    stringToStyle = dictionaryString(R.string.videoLink_beCareful_label),
                    style = SpanStyle(
                        fontWeight = OddOneOutTheme.typography.Body.B700.style.fontWeight,
                        textDecoration = TextDecoration.Underline
                    )
                )

                Text(text = styledString)

                VerticalSpacerD800()

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

                    HorizontalSpacerD600()

                    SelectionContainer {

                        val annotatedLink = link
                            .addClickableUrl(linkText = link, url = link)

                        ClickableText(
                            modifier = modifier.fillMaxWidth(),
                            text = annotatedLink,
                            onClick = { onVideoLinkClicked(bottomSheetState) },
                            style = OddOneOutTheme.typography.Body.B700.style.copy(color = OddOneOutTheme.colors.text.color),
                        )
                    }
                }
            }
        },
        stickyBottomContent = {
            if (canOpenVideoLink) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
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
@Preview
private fun VideoLinkBottomSheetPreview() {
    Preview {
        val sheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)
        VideoLinkBottomSheet(
            bottomSheetState = sheetState,
            link = "https://www.google.com",
            onDismiss = { }
        )
    }
}