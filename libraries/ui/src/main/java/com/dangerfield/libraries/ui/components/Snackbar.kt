package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.HorizontalSpacerD600
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.color.ColorResource
import com.dangerfield.libraries.ui.color.ProvideContentColor
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonSize
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.elevation
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import androidx.compose.material3.SnackbarData as MaterialSnackbarData
import androidx.compose.material3.SnackbarDuration as MaterialSnackbarDuration

@Composable
fun Snackbar(
    oddOneOutSnackbarData: OddOneOutSnackbarData,
    modifier: Modifier = Modifier,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: ColorResource = SnackbarDefaults.color,
    contentColor: ColorResource = SnackbarDefaults.contentColor,
) {
    val message = oddOneOutSnackbarData.visuals.message
    val title = oddOneOutSnackbarData.visuals.title
    val actionLabel = oddOneOutSnackbarData.visuals.actionLabel
    val isDebugMessage = oddOneOutSnackbarData.visuals.isDebug

    ProvideContentColor(color = contentColor) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Dimension.D500, vertical = Dimension.D1200)
                .elevation(Elevation.Button, shape = shape)
                .clip(shape)
                .border(
                    shape = shape,
                    color = OddOneOutTheme.colors.accent.color,
                    width = Dimension.D50
                )
                .background(containerColor.color)
                .padding(Dimension.D800)
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (title != null) {
                    Row(verticalAlignment = CenterVertically) {
                        if (isDebugMessage) {
                            Icon(spyfallIcon = SpyfallIcon.Bug(null))
                            HorizontalSpacerD600()
                        }

                        Text(text = title)

                    }

                    VerticalSpacerD500()
                }

                VerticalSpacerD500()

                Row(verticalAlignment = CenterVertically) {
                    if (isDebugMessage && title.isNullOrBlank()) {
                        Icon(spyfallIcon = SpyfallIcon.Bug(null))
                        HorizontalSpacerD600()
                    }

                    Text(
                        text = message,
                        typography = OddOneOutTheme.typography.Body.B600,
                    )
                }

                VerticalSpacerD800()

                if (actionLabel != null) {
                    Button(
                        onClick = oddOneOutSnackbarData::performAction,
                        size = ButtonSize.ExtraSmall,
                    ) {
                        Text(text = actionLabel)
                    }
                }
            }


            if (oddOneOutSnackbarData.visuals.withDismissAction) {
                IconButton(
                    icon = SpyfallIcon.Close("Close"),
                    onClick = oddOneOutSnackbarData::dismiss,
                )
            }
        }
    }
}

@Composable
fun snackBarData(
    title: String? = null,
    message: String,
    actionLabel: String? = null,
    isDebug: Boolean = false,
    withDismissAction: Boolean = true,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onAction: () -> Unit = {},
    onDismiss: () -> Unit = {},
): OddOneOutSnackbarData = object : OddOneOutSnackbarData {
    override val visuals: OddOneOutSnackbarVisuals = OddOneOutSnackbarVisuals(
        title = title,
        isDebug = isDebug,
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration.fromMaterial()
    )

    override fun performAction() {
        onAction()
    }

    override fun dismiss() {
        onDismiss()
    }
}

fun oddOneOutSnackbarData(
    visuals: OddOneOutSnackbarVisuals,
    onAction: () -> Unit = {},
    onDismiss: () -> Unit = {},
): OddOneOutSnackbarData = object : OddOneOutSnackbarData {
    override val visuals: OddOneOutSnackbarVisuals = visuals

    override fun performAction() {
        onAction()
    }

    override fun dismiss() {
        onDismiss()
    }
}

@Preview
@Composable
private fun PreviewSnackbarDebug() {
    Preview {
        Snackbar(
            oddOneOutSnackbarData = snackBarData(
                title = "Title",
                message = "This is some text that describes something that takes up a lot of space.".repeat(
                    2
                ),
                isDebug = true,
                actionLabel = "Do something",
                withDismissAction = true,
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSnackbar2() {
    Preview {
        Snackbar(
            oddOneOutSnackbarData = snackBarData(
                title = "Title",
                message = "This is some text that describes something that takes up a lot of space.".repeat(
                    2
                ),
                actionLabel = "Do something",
                withDismissAction = true,
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSnackbarDeveloper() {
    Preview {
        Snackbar(
            oddOneOutSnackbarData = snackBarData(
                message = "Hello World",
                isDebug = true
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSnackbar() {
    Preview {
        Snackbar(
            oddOneOutSnackbarData = snackBarData(
                message = "Hello World",
                isDebug = false
            )
        )
    }
}

object SnackbarDefaults {
    /** Default shape of a snackbar. */
    val shape: Shape @Composable get() = Radii.Banner.shape

    /** Default color of a snackbar. */
    val color: ColorResource @Composable get() = OddOneOutTheme.colors.surfacePrimary

    /** Default content color of a snackbar. */
    val contentColor: ColorResource @Composable get() = OddOneOutTheme.colors.text
}

fun MaterialSnackbarData.toSnackbarData(title: String): OddOneOutSnackbarData {
    val visuals = OddOneOutSnackbarVisuals(
        title = title,
        message = this.visuals.message,
        actionLabel = this.visuals.actionLabel,
        withDismissAction = this.visuals.withDismissAction,
        duration = this.visuals.duration
    )

    return object : OddOneOutSnackbarData {
        override val visuals: OddOneOutSnackbarVisuals = visuals

        override fun dismiss() {
            this@toSnackbarData.dismiss()
        }

        override fun performAction() {
            this@toSnackbarData.performAction()
        }
    }
}

@Stable
interface OddOneOutSnackbarData {
    val visuals: OddOneOutSnackbarVisuals

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
data class OddOneOutSnackbarVisuals(
    val title: String?,
    val isDebug: Boolean = false,
    override val message: String,
    override val actionLabel: String?,
    override val withDismissAction: Boolean,
    override val duration: androidx.compose.material3.SnackbarDuration
) : SnackbarVisuals


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

    fun fromMaterial(): MaterialSnackbarDuration {
        return when (this) {
            Short -> MaterialSnackbarDuration.Short
            Long -> MaterialSnackbarDuration.Long
            Indefinite -> MaterialSnackbarDuration.Indefinite
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