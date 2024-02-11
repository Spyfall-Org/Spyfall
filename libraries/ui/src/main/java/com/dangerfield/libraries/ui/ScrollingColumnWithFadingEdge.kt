package com.dangerfield.libraries.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ScrollingColumnWithFadingEdge(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    fadingEdgeLength: Dp = FadingEdgeLengthLarge,
    fadingEdgeDuration: Duration = 750.milliseconds,
    fadingEdgeMinimumScrollAmountToShow: Dp = 0.dp,
    fadingEdgeColor: Color = OddOneOutTheme.colorScheme.background.color,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val canScroll by remember(density) {
        derivedStateOf {
            with(density) {
                state.maxValue > fadingEdgeMinimumScrollAmountToShow.toPx() && state.maxValue != Int.MAX_VALUE
            }
        }
    }

    var hasScrolled by rememberSaveable { mutableStateOf(false) }

    if (state.value != 0 && !hasScrolled) {
        LaunchedEffect(Unit) {
            hasScrolled = true
        }
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state)
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
        AnimatedVisibility(
            visible = !hasScrolled && canScroll,
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = fadingEdgeDuration.inWholeMilliseconds.toInt()
                )
            ),
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 0
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = fadingEdgeLength
                )
                .align(
                    alignment = Alignment.BottomCenter
                )
        ) {
            Spacer(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                fadingEdgeColor
                            )
                        )
                    )
            )
        }
    }
}

@Preview(group = "LazyColumn")
@Composable
private fun ColumnWithFadingEdgePreview() {
    PreviewContent {
        ScrollingColumnWithFadingEdge {
            repeat(times = 100) {
                Text("Item $it")
            }
        }
    }
}

internal val FadingEdgeLengthLarge = 150.dp
