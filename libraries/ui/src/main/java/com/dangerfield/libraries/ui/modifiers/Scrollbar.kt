package com.dangerfield.libraries.ui.modifiers


import android.util.LayoutDirection
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.Spacing
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

fun Modifier.drawVerticalScrollbar(
    state: ScrollState,
    color: Color,
    reverseScrolling: Boolean = false,
): Modifier = drawScrollbar(state, Orientation.Vertical, color, reverseScrolling)

private fun Modifier.drawScrollbar(
    state: ScrollState,
    orientation: Orientation,
    color: Color,
    reverseScrolling: Boolean,
): Modifier = drawScrollbar(
    orientation,
    color,
    reverseScrolling
) { reverseDirection, atEnd, color, alpha ->
    if (state.maxValue > 0) {
        // size of the visible view
        val canvasSize = if (orientation == Orientation.Horizontal) size.width else size.height
        // size of the visible and obscured view
        val totalSize = canvasSize + state.maxValue
        // size of scrollbar relative to scrollable area
        val thumbSize = canvasSize / totalSize * canvasSize
        // position
        val startOffset = state.value / totalSize * canvasSize
        drawScrollbar(orientation, reverseDirection, atEnd, color, alpha, thumbSize, startOffset)
    }
}

@Suppress("MagicNumber")
private fun DrawScope.drawScrollbar(
    orientation: Orientation,
    reverseDirection: Boolean,
    atEnd: Boolean,
    color: Color,
    alpha: Float,
    thumbSize: Float,
    startOffset: Float,
) {
    val thicknessPx = Spacing.S100.toPx()
    val topLeft = if (orientation == Orientation.Horizontal) {
        Offset(
            x = if (reverseDirection) size.width - startOffset - thumbSize else startOffset,
            y = if (atEnd) size.height - thicknessPx else 0f
        )
    } else {
        Offset(
            x = if (atEnd) size.width - thicknessPx else 0f,
            y = if (reverseDirection) size.height - startOffset - thumbSize else startOffset
        )
    }

    val size = if (orientation == Orientation.Horizontal) {
        Size(thumbSize, thicknessPx)
    } else {
        Size(thicknessPx, thumbSize)
    }

    drawRoundRect(
        color = color,
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(10f, 10f),
        alpha = alpha
    )
}

private fun Modifier.drawScrollbar(
    orientation: Orientation,
    color: Color,
    reverseScrolling: Boolean,
    onDraw: DrawScope.(
        reverseDirection: Boolean,
        atEnd: Boolean,
        color: Color,
        alpha: Float,
    ) -> Unit,
): Modifier = composed {
    val scrolled = remember {
        MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    val nestedScrollConnection = remember(orientation, scrolled) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = if (orientation == Orientation.Horizontal) consumed.x else consumed.y
                if (delta != 0f) scrolled.tryEmit(Unit)
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return available
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return available
            }
        }
    }

    val isLtr = LocalLayoutDirection.current.ordinal == LayoutDirection.LTR

    val reverseDirection = if (orientation == Orientation.Horizontal) {
        if (isLtr) reverseScrolling else !reverseScrolling
    } else reverseScrolling

    val atEnd = if (orientation == Orientation.Vertical) isLtr else true

    Modifier
        .nestedScroll(nestedScrollConnection)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                onDraw(
                    reverseDirection,
                    atEnd,
                    color,
                    1f
                )
            }
        }
}

@Preview
@Composable
private fun TextWithScrollbarPreview() {
    val scrollState = rememberScrollState()

    PreviewContent {
        Surface(
            modifier = Modifier
                .drawVerticalScrollbar(state = scrollState, color = OddOneOutTheme.colorScheme.border.color, reverseScrolling = false)
        ) {
            Text(
                text = "This is a very long sentence".repeat(50),
                modifier = Modifier
                    .heightIn(50.dp)
                    .verticalScroll(
                        state = scrollState,
                        enabled = true
                    ),
                color = OddOneOutTheme.colorScheme.textWarning
            )
        }
    }
}
