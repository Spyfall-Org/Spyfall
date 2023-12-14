package com.dangerfield.libraries.ui.tooltip

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.dangerfield.libraries.coreflowroutines.cancelledJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.dangerfield.libraries.ui.PreviewContent
import spyfallx.ui.R
import com.dangerfield.libraries.ui.Spacing
import spyfallx.ui.color.background
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import kotlin.time.Duration.Companion.milliseconds

/**
 * A tooltip that provides a descriptive message for an anchor.
 *
 * Tooltips are invoked when the anchor is long pressed.
 *
 * @param tooltip the composable that will be used to populate the tooltip's content. Should contain a [Text].
 * @param modifier the [Modifier] to be applied to the tooltip.
 * @param tooltipState handles the state of the tooltip's visibility.
 * @param content the composable that the tooltip will anchor to.
 */
@SuppressLint("PrivateResource")
@Composable
fun TooltipBox(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    tooltipState: TooltipState = remember { TooltipState() },
    content: @Composable TooltipBoxScope.() -> Unit,
) {
    val tooltipAnchorPadding = with(LocalDensity.current) { Spacing.S100.roundToPx() }
    val positionProvider = remember { TooltipPositionProvider(tooltipAnchorPadding) }
    val isPressed = remember(tooltipState) { MutableStateFlow(false) }
    val coroutineScope = rememberCoroutineScope()
    val longPressLabel = ""
    LaunchedEffect(tooltipState) {
        var showJob = cancelledJob
        isPressed.collect { pressed ->
            showJob.cancel()
            if (pressed) {
                showJob = launch { TooltipManager.showForever(tooltipState) }
                delay(TooltipDuration)
            }
        }
    }

    val scope = remember(tooltipState) {
        object : TooltipBoxScope {
            override fun Modifier.tooltipAnchor(): Modifier =
                this
                    .pointerInput(tooltipState) {
                        // We don't use `detectTapGestures` because it consumes the events which we don't want
                        awaitEachGesture {
                            val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                            val pass = PointerEventPass.Initial

                            // wait for the first down press
                            awaitFirstDown(pass = pass)

                            try {
                                // listen to if there is up gesture within the longPressTimeout limit
                                withTimeout(longPressTimeout) {
                                    waitForUpOrCancellation(pass = pass)
                                }
                            } catch (_: PointerEventTimeoutCancellationException) {
                                // handle long press - Show the tooltip
                                isPressed.value = true
                                try {
                                    // consume the children's click handling
                                    awaitPointerEvent(pass = pass)
                                        .changes
                                        .forEach { it.consume() }
                                } finally {
                                    isPressed.value = false
                                }
                            }
                        }
                    }
                    .semantics(mergeDescendants = true) {
                        onLongClick(
                            label = longPressLabel,
                            action = {
                                coroutineScope.launch {
                                    tooltipState.show()
                                }
                                true
                            }
                        )
                    }
        }
    }

    // This box is here so that we always emit a single composable
    Box(modifier = modifier) {
        val transition = updateTransition(tooltipState.isVisible, label = "TooltipTransition")
        if (transition.currentState || transition.targetState) {
            Popup(
                popupPositionProvider = positionProvider,
                onDismissRequest = {
                    if (tooltipState.isVisible) {
                        coroutineScope.launch { tooltipState.dismiss() }
                    }
                },
                properties = PopupProperties(focusable = true)
            ) {
                TooltipContent(modifier = Modifier.animateTooltip(transition), content = tooltip)
            }
        }
        scope.content()
    }
}

@SuppressLint("PrivateResource")
@Composable
private fun TooltipContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .semantics { paneTitle = context.getString(R.string.tool_tip_pane_title) }
            .background(OddOneOutTheme.colorScheme.surfacePrimary)
            .padding(Spacing.S800)
    ) {
        content()
    }
}

@Suppress("MagicNumber")
private fun Modifier.animateTooltip(
    transition: Transition<Boolean>,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "animateTooltip"
        properties["transition"] = transition
    }
) {
    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // show tooltip
                tween(
                    durationMillis = TooltipFadeInDuration.inWholeMilliseconds.toInt(),
                    easing = LinearOutSlowInEasing
                )
            } else {
                // dismiss tooltip
                tween(
                    durationMillis = TooltipFadeOutDuration.inWholeMilliseconds.toInt(),
                    easing = LinearOutSlowInEasing
                )
            }
        },
        label = "tooltip transition: scaling"
    ) { if (it) 1f else 0.8f }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // show tooltip
                tween(
                    durationMillis = TooltipFadeInDuration.inWholeMilliseconds.toInt(),
                    easing = LinearEasing
                )
            } else {
                // dismiss tooltip
                tween(
                    durationMillis = TooltipFadeOutDuration.inWholeMilliseconds.toInt(),
                    easing = LinearEasing
                )
            }
        },
        label = "TooltipAlpha"
    ) { if (it) 1f else 0f }

    graphicsLayer(
        scaleX = scale,
        scaleY = scale,
        alpha = alpha
    )
}

private class TooltipPositionProvider(
    val tooltipAnchorPadding: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2

        // Tooltip prefers to be above the anchor,
        // but if this causes the tooltip to overlap with the anchor
        // then we place it below the anchor
        var y = anchorBounds.top - popupContentSize.height - tooltipAnchorPadding
        if (y < 0)
            y = anchorBounds.bottom + tooltipAnchorPadding
        return IntOffset(x, y)
    }
}

private val TooltipFadeInDuration = 150.milliseconds
private val TooltipFadeOutDuration = 75.milliseconds

@Preview
@Composable
private fun TooltipBoxPreview() {
    PreviewContent(contentPadding = PaddingValues(Spacing.S1000)) {
        TooltipBox(tooltip = { Text("Tooltip") }) {
            Text(
                text = "Long Press Me",
                modifier = Modifier.tooltipAnchor(),
                typographyToken = OddOneOutTheme.typography.Default
            )
        }
    }
}

@Preview
@Composable
private fun TooltipContentPreview() {
    PreviewContent(contentPadding = PaddingValues(Spacing.S300)) {
        TooltipContent {
            Text("Tooltip")
        }
    }
}
