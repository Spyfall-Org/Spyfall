package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.HorizontalSpacerS600
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Sizes
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.ThemePreviews
import com.dangerfield.libraries.ui.VerticalSpacerS500
import com.dangerfield.libraries.ui.color.ProvideContentColor
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.elevation
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import spyfallx.core.DebugSnackMessageToken
import spyfallx.ui.color.ColorToken
import spyfallx.ui.color.background
import spyfallx.ui.color.border
import androidx.compose.material3.SnackbarData as MaterialSnackbarData
import androidx.compose.material3.SnackbarDuration as MaterialSnackbarDuration

@Composable
fun Snackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    isDebugMessage: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: ColorToken.Color = SnackbarDefaults.color,
    contentColor: ColorToken.Color = SnackbarDefaults.contentColor,
    actionContentColor: ColorToken.Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: ColorToken.Color = SnackbarDefaults.dismissActionContentColor,
) {
    val message = snackbarData.visuals.message.replace(DebugSnackMessageToken, "")

    ProvideContentColor(color = contentColor) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.S500, vertical = Spacing.S1200)
                .elevation(Elevation.Fixed, shape)
                .clip(shape)
                .border(
                    shape,
                    OddOneOutTheme.colorScheme.accent,
                    width = Sizes.S50
                )
                .background(containerColor)
                .padding(Spacing.S800)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isDebugMessage) {
                    Icon(spyfallIcon = SpyfallIcon.Bug(null))
                }

                HorizontalSpacerS600()

                Text(
                    text = if (isDebugMessage) "Debug Message" else "",
                    modifier = Modifier.weight(1f)
                )

                HorizontalSpacerS600()
                IconButton(
                    icon = SpyfallIcon.Close("Close"),
                    onClick = snackbarData::dismiss,
                )
            }

            VerticalSpacerS500()

            Text(text = message, typographyToken = OddOneOutTheme.typography.Body.B600)

        }
    }
}

@Composable
fun snackBarData(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = true,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onAction: () -> Unit = {},
    onDismiss: () -> Unit = {},
): SnackbarData = object : SnackbarData {
    override val visuals: SnackbarVisuals = object : SnackbarVisuals {
        override val message: String = message
        override val actionLabel: String? = actionLabel
        override val withDismissAction: Boolean = withDismissAction
        override val duration: SnackbarDuration = duration
    }

    override fun performAction() {
        onAction()
    }

    override fun dismiss() {
        onDismiss()
    }
}

@ThemePreviews
@Composable
private fun PreviewSnackbarFilled() {
    PreviewContent {
        Snackbar(
            isDebugMessage = true,
            snackbarData = snackBarData(
                message = "Hello World",
                actionLabel = "Do something",
                withDismissAction = true,
            )
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewSnackbar() {
    PreviewContent {
        Snackbar(snackbarData = snackBarData(message = "Hello World"))
    }
}

fun MaterialSnackbarData.isDebugMessage(): Boolean {
    return this.visuals.message.contains(DebugSnackMessageToken)
}

object SnackbarDefaults {
    /** Default shape of a snackbar. */
    val shape: Shape @Composable get() = Radii.Banner.shape

    /** Default color of a snackbar. */
    val color: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.surfacePrimary

    /** Default content color of a snackbar. */
    val contentColor: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.text

    /** Default action content color of a snackbar. */
    val actionContentColor: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.accent

    /** Default dismiss action content color of a snackbar. */
    val dismissActionContentColor: ColorToken.Color @Composable get() = OddOneOutTheme.colorScheme.text
}

fun MaterialSnackbarData.toSnackbarData(): SnackbarData {
    val visuals: SnackbarVisuals = object : SnackbarVisuals {
        override val actionLabel: String? = this@toSnackbarData.visuals.actionLabel
        override val duration: SnackbarDuration =
            this@toSnackbarData.visuals.duration.toSnackbarDuration()
        override val message: String = this@toSnackbarData.visuals.message
        override val withDismissAction: Boolean =
            this@toSnackbarData.visuals.withDismissAction
    }

    return object : SnackbarData {
        override val visuals: SnackbarVisuals = visuals

        override fun dismiss() {
            this@toSnackbarData.dismiss()
        }

        override fun performAction() {
            this@toSnackbarData.performAction()
        }
    }
}

@Stable
interface SnackbarData {
    val visuals: SnackbarVisuals

    /**
     * Function to be called when Snackbar action has been performed to notify the listeners.
     */
    fun performAction()

    /**
     * Function to be called when Snackbar is dismissed either by timeout or by the user.
     */
    fun dismiss()
}

@Stable
interface SnackbarVisuals {
    val message: String
    val actionLabel: String?
    val withDismissAction: Boolean
    val duration: SnackbarDuration
}

enum class SnackbarDuration {
    /**
     * Show the Snackbar for a short period of time
     */
    Short,

    /**
     * Show the Snackbar for a long period of time
     */
    Long,

    /**
     * Show the Snackbar indefinitely until explicitly dismissed or action is clicked
     */
    Indefinite;

    fun toMaterial(): androidx.compose.material3.SnackbarDuration {
        return when (this) {
            Short -> androidx.compose.material3.SnackbarDuration.Short
            Long -> androidx.compose.material3.SnackbarDuration.Long
            Indefinite -> androidx.compose.material3.SnackbarDuration.Indefinite
        }
    }
}

fun MaterialSnackbarDuration.toSnackbarDuration(): SnackbarDuration {
    return when (this) {
        androidx.compose.material3.SnackbarDuration.Short -> SnackbarDuration.Short
        androidx.compose.material3.SnackbarDuration.Long -> SnackbarDuration.Long
        androidx.compose.material3.SnackbarDuration.Indefinite -> SnackbarDuration.Indefinite
    }
}