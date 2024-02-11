package com.dangerfield.libraries.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class SlideFrom {
    LEFT, RIGHT
}

@Suppress("UnusedPrivateMember")
@Composable
fun SlideInContent(
    modifier: Modifier = Modifier,
    slideFrom: SlideFrom = SlideFrom.LEFT,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val initialOffsetX: (fullWidth: Int) -> Int = when (slideFrom) {
        SlideFrom.LEFT -> { fullWidth -> -fullWidth }
        SlideFrom.RIGHT -> { fullWidth -> fullWidth }
    }

    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(
            initialOffsetX = initialOffsetX,
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = initialOffsetX,
            animationSpec = tween(durationMillis = 300)
        ),
        content = content,
        modifier = modifier
    )
}