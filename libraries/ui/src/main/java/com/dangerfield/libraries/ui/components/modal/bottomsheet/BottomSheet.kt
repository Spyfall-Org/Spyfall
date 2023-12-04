package com.dangerfield.libraries.ui.components.modal.bottomsheet

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Sizes
import com.dangerfield.libraries.ui.align
import com.dangerfield.libraries.ui.clip
import com.dangerfield.libraries.ui.color.LocalColorScheme
import com.dangerfield.libraries.ui.color.ProvideContentColor
import com.dangerfield.libraries.ui.components.icon.CircularIcon
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.elevation
import com.dangerfield.libraries.ui.plus
import com.dangerfield.libraries.ui.rememberWithKey
import com.dangerfield.libraries.ui.theme.SpyfallTheme
import com.dangerfield.libraries.ui.withSize
import kotlinx.coroutines.launch
import spyfallx.ui.Spacing
import spyfallx.ui.color.background
import spyfallx.ui.inset

/**
 * A bottom sheet component is a bottom sheet like component that is used to display content in a sheet that slides in
 * from the bottom, covering the content behind it.
 *
 * @param onDismissRequest Called to remove the sheet from composition. Not the same as hiding the sheet
 * @param modifier The modifier.
 * @param state The [BottomSheetState].
 * @param content The content.
 */
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: BottomSheetState = rememberBottomSheetState(),
    topAccessory: TopAccessory = dragHandleTopAccessory(),
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit,
) {
    val backgroundColor = SpyfallTheme.colorScheme.background
    val contentColor = SpyfallTheme.colorScheme.onBackground
    val shape = bottomSheetShape(topAccessory)

    val systemBarVerticalInsets: WindowInsets =  WindowInsets.systemBars.only(WindowInsetsSides.Vertical)

    ModalBottomSheet(
        modifier = Modifier
            .offset {
                IntOffset(
                    0,
                    state.sheetState
                        .requireOffset()
                        .toInt()
                )
            }
            .elevation(Elevation.Fixed, shape, clip = false)
            .offset {
                IntOffset(
                    0,
                    -state.sheetState
                        .requireOffset()
                        .toInt()
                )
            },
        onDismissRequest = onDismissRequest,
        sheetState = state.sheetState,
        shape = shape,
        windowInsets = systemBarVerticalInsets,
        containerColor = backgroundColor.color,
        scrimColor = SpyfallTheme.colorScheme.backgroundOverlay.color,
        tonalElevation = 0.dp,
        dragHandle = null
    ) {
        ProvideContentColor(contentColor) {
            BottomSheetContent(
                sheetState = state,
                onDismissRequest = onDismissRequest,
                topAccessory = topAccessory,
                contentAlignment = contentAlignment,
                content = content,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    sheetState: BottomSheetState,
    onDismissRequest: () -> Unit,
    topAccessory: TopAccessory,
    contentAlignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box {
        Column(
            modifier = modifier.padding(top = topAccessory.size / 2),
            horizontalAlignment = contentAlignment,
            content = content
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f),
            contentAlignment = Alignment.TopCenter
        ) {
            TopAccessory(topAccessory, sheetState, onDismissRequest)
        }
    }
}

@Composable
private fun TopAccessory(
    topAccessory: TopAccessory,
    sheetState: BottomSheetState,
    onDismissRequest: () -> Unit,
) {
    when (topAccessory) {
        TopAccessory.DragHandle -> DragHandle(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest
        )

        is TopAccessory.Icon -> TopAccessory {
            Box(
                Modifier
                    .background(topAccessory.backgroundColor)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    spyfallIcon = topAccessory.icon,
                    tint = topAccessory.color,
                    iconSize = IconSize.Medium
                )
            }
        }
    }
}

@SuppressLint("PrivateResource")
@Composable
private fun DragHandle(
    sheetState: BottomSheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val animateToDismiss = {
        if (sheetState.confirmValueChange(BottomSheetValue.Hidden)) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }
    }
    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                dismiss("dismiss") {
                    animateToDismiss()
                    true
                }
            }
            .fillMaxWidth()
            .padding(vertical = Spacing.S300),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(Spacing.S1400, 5.dp)
                .background(
                    color = SpyfallTheme.colorScheme.onBackground,
                    radius = Radii.Round
                )
        )
    }
}

@Composable
private fun TopAccessory(
    modifier: Modifier = Modifier,
    icon: SpyfallIcon? = null,
    content: @Composable () -> Unit,
) {
    BadgedBox(
        modifier = modifier
            .padding(TopIconBorderWidth),
        badge = {
            if (icon != null) {
                CircularIcon(
                    icon = icon,
                    iconSize = IconSize.Small,
                    padding = Spacing.S400,
                    backgroundColor = LocalColorScheme.current.background,
                    contentColor = SpyfallTheme.colorScheme.onBackground,
                    elevation = Elevation.Fixed
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .size(TopIconSize + TopIconPadding * 2 - TopIconBorderWidth * 2)
                .fillMaxSize()
                .clip(Radii.Round),
            contentAlignment = Alignment.Center,
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}

@Composable
private fun bottomSheetShape(topAccessory: TopAccessory): Shape = rememberWithKey(
    topAccessory.shape,
    topAccessory.size
) { accessoryShape, accessorySize ->
    val baseShape = Radii.BottomSheet.shape

    if (accessoryShape == null) {
        baseShape
    } else {
        baseShape.inset(top = accessorySize / 2) + accessoryShape.withSize(accessorySize)
            .align(Alignment.TopCenter)
    }
}

private val TopAccessory.size: Dp
    get() = when (this) {
        is TopAccessory.Icon -> TopIconSize + TopIconPadding * 2
        is TopAccessory.DragHandle -> 0.dp
    }

private val TopAccessory.shape: Shape?
    get() = when (this) {
        is TopAccessory.Icon -> CircleShape
        is TopAccessory.DragHandle -> null
    }

private val TopIconPadding = Spacing.S800
private val TopIconSize = Sizes.S1100
private val TopIconBorderWidth = Sizes.S100

@Preview(heightDp = 300)
@Composable
private fun PreviewBottomSheetIcon(
) {
    PreviewContent {
        BottomSheet(
            onDismissRequest = {},
            topAccessory = TopAccessory.Icon(
                icon = SpyfallIcon.VideoCall("Video"),
                color = SpyfallTheme.colorScheme.background.color,
                backgroundColor = SpyfallTheme.colorScheme.onBackground.color
            ),
            state = rememberBottomSheetState(BottomSheetValue.Expanded),
        ) {
            Text(
                text = "Content",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = Spacing.S1400,
                        horizontal = Spacing.S400
                    ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(heightDp = 300)
@Composable
private fun PreviewBottomSheetDragHandle(
) {
    PreviewContent {
        BottomSheet(
            onDismissRequest = {},
            topAccessory = TopAccessory.DragHandle,
            state = rememberBottomSheetState(BottomSheetValue.Expanded),
        ) {
            Text(
                text = "Content",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = Spacing.S1400,
                        horizontal = Spacing.S400
                    ),
                textAlign = TextAlign.Center
            )
        }
    }
}



