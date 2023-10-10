package spyfallx.coreui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import spyfallx.coreui.Elevation
import spyfallx.coreui.color.LocalTypography
import spyfallx.coreui.theme.SpyfallTheme
import spyfallx.coreui.thenIf
import androidx.compose.material3.LargeTopAppBar as MaterialLargeTopAppBar
import androidx.compose.material3.MediumTopAppBar as MaterialMediumTopAppBar
import androidx.compose.material3.TopAppBar as MaterialTopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior as MaterialTopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState as MaterialTopAppBarState
import androidx.compose.material3.rememberTopAppBarState as materialRememberTopAppBarState

@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SpyfallTheme.colorScheme.surfacePrimary.color,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    liftOnScroll: Boolean = scrollBehavior != null,
) {
    MaterialTopAppBar(
        title = {
            ProvideTextStyle(value = SpyfallTheme.typography.Heading.H600.style, content = title)
        },
        modifier = modifier
            .thenIf(liftOnScroll) { liftOnScroll(scrollBehavior) },
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior?.delegate,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = backgroundColor,
            scrolledContainerColor = backgroundColor
        )
    )
}

@Composable
fun MediumTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SpyfallTheme.colorScheme.surfacePrimary.color,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    liftOnScroll: Boolean = scrollBehavior != null,
) {
    MaterialMediumTopAppBar(
        title = title,
        modifier = modifier
            .thenIf(liftOnScroll) { liftOnScroll(scrollBehavior) },
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior?.delegate,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = backgroundColor,
            scrolledContainerColor = backgroundColor
        )
    )
}

@Composable
fun LargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SpyfallTheme.colorScheme.surfacePrimary.color,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    liftOnScroll: Boolean = scrollBehavior != null,
) {
    MaterialLargeTopAppBar(
        title = title,
        modifier = modifier
            .thenIf(liftOnScroll) { liftOnScroll(scrollBehavior) },
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior?.delegate,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = backgroundColor,
            scrolledContainerColor = backgroundColor
        )
    )
}

private fun Modifier.liftOnScroll(
    scrollBehavior: TopAppBarScrollBehavior?,
): Modifier {
    requireNotNull(scrollBehavior) {
        "To use liftOnScroll you need to pass a scrollBehavior"
    }
    return composed {
        val elevation by animateDpAsState(
            if (scrollBehavior.delegate.state.overlappedFraction > 0.01f) {
                Elevation.AppBar.dp
            } else {
                0.dp
            }, label = ""
        )
        Modifier.shadow(elevation)
    }
}

// @JvmInline
@Stable
class TopAppBarState internal constructor(
    internal val delegate: MaterialTopAppBarState,
) {
    var heightOffsetLimit: Float
        get() = delegate.heightOffsetLimit
        set(value) {
            delegate.heightOffsetLimit = value
        }

    var heightOffset: Float
        get() = delegate.heightOffset
        set(value) {
            delegate.heightOffset = value
        }

    var contentOffset: Float
        get() = delegate.contentOffset
        set(value) {
            delegate.contentOffset = value
        }

    val collapsedFraction: Float
        get() = delegate.collapsedFraction

    val overlappedFraction: Float
        get() = delegate.overlappedFraction
}

@Composable
fun rememberTopAppBarState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f,
) = TopAppBarState(
    materialRememberTopAppBarState(
        initialHeightOffsetLimit = initialHeightOffsetLimit,
        initialHeightOffset = initialHeightOffset,
        initialContentOffset = initialContentOffset
    )
)

@JvmInline
@Stable
value class TopAppBarScrollBehavior(
    internal val delegate: MaterialTopAppBarScrollBehavior,
) {
    val isPinned: Boolean
        get() = delegate.isPinned
    val snapAnimationSpec: AnimationSpec<Float>?
        get() = delegate.snapAnimationSpec
    val flingAnimationSpec: DecayAnimationSpec<Float>?
        get() = delegate.flingAnimationSpec
    val nestedScrollConnection: NestedScrollConnection
        get() = delegate.nestedScrollConnection

    companion object {
        @Composable
        fun pinnedScrollBehavior(
            state: TopAppBarState = rememberTopAppBarState(),
            canScroll: () -> Boolean = { true },
        ) = TopAppBarScrollBehavior(
            TopAppBarDefaults.pinnedScrollBehavior(
                state = state.delegate,
                canScroll = canScroll
            )
        )

        @Composable
        fun enterAlwaysScrollBehavior(
            state: TopAppBarState = rememberTopAppBarState(),
            canScroll: () -> Boolean = { true },
            snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
            flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
        ): TopAppBarScrollBehavior =
            TopAppBarScrollBehavior(
                TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = state.delegate,
                    canScroll = canScroll,
                    snapAnimationSpec = snapAnimationSpec,
                    flingAnimationSpec = flingAnimationSpec
                )
            )

        @Composable
        fun exitUntilCollapsedScrollBehavior(
            state: TopAppBarState = rememberTopAppBarState(),
            canScroll: () -> Boolean = { true },
            snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
            flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
        ): TopAppBarScrollBehavior =
            TopAppBarScrollBehavior(
                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    state = state.delegate,
                    snapAnimationSpec = snapAnimationSpec,
                    flingAnimationSpec = flingAnimationSpec,
                    canScroll = canScroll
                )
            )
    }
}
