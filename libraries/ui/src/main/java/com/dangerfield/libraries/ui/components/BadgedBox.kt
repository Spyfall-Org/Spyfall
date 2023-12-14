package com.dangerfield.libraries.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.ui.Elevation
import com.dangerfield.libraries.ui.PreviewContent
import com.dangerfield.libraries.ui.components.BadgeTokens.LargeLabelTextStyle
import com.dangerfield.libraries.ui.components.icon.CircularIcon
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.Radius
import com.dangerfield.libraries.ui.Spacing
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun BadgedBox(
    badge: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    contentRadius: Radius = Radii.None,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current

    Layout(
        {
            Box(
                modifier = Modifier.layoutId("anchor"),
                contentAlignment = Alignment.Center,
                content = content
            )
            Box(
                modifier = Modifier.layoutId("badge"),
                content = badge
            )
        },
        modifier = modifier
    ) { measurables, constraints ->

        val badgePlaceable = measurables.first { it.layoutId == "badge" }.measure(
            // Measure with loose constraints for height as we don't want the text to take up more
            // space than it needs.
            constraints.copy(minHeight = 0)
        )

        val anchorPlaceable = measurables.first { it.layoutId == "anchor" }.measure(constraints)

        val firstBaseline = anchorPlaceable[FirstBaseline]
        val lastBaseline = anchorPlaceable[LastBaseline]
        val totalWidth = anchorPlaceable.width
        val totalHeight = anchorPlaceable.height

        val badgeOffset = calculateBadgeOffset(
            cornerSize = contentRadius.cornerSize,
            density = density,
            contentWidth = totalWidth.dp.value,
            contentHeight = totalHeight.dp.value
        )

        layout(
            totalWidth,
            totalHeight,
            // Provide custom baselines based only on the anchor content to avoid default baseline
            // calculations from including by any badge content.
            mapOf(
                FirstBaseline to firstBaseline,
                LastBaseline to lastBaseline
            )
        ) {

            // Use the width of the badge to infer whether it has any content (based on radius used
            // in [Badge]) and determine its horizontal offset.
            val badeHasContent = badgePlaceable.width > (BadgeTokens.Size.roundToPx())
            val badgeHorizontalOffset =
                if (badeHasContent) BadgeWithContentHorizontalOffset else BadgeOffset
            val badgeVerticalOffset =
                if (badeHasContent) BadgeWithContentVerticalOffset else BadgeOffset

            anchorPlaceable.placeRelative(0, 0)
            val badgeX = (anchorPlaceable.width + badgeHorizontalOffset.roundToPx()) - badgeOffset.x.roundToInt()
            val badgeY = (-badgePlaceable.height / 2 + badgeVerticalOffset.roundToPx()) + badgeOffset.y.roundToInt()

            badgePlaceable.placeRelative(
                badgeX,
                badgeY
            )
        }
    }
}

private fun calculateBadgeOffset(
    cornerSize: CornerSize,
    density: Density,
    contentWidth: Float,
    contentHeight: Float
): Offset {
    val cornerSizepx = cornerSize.toPx(Size(contentWidth, contentHeight), density)

    val radius = cornerSizepx

    val offset = radius - (radius * sqrt(2.0f) / 2)

    return Offset(offset, offset)
}

/**
 * A badge represents dynamic information such as a number of pending requests in a navigation bar.
 *
 * Badges can be icon only or contain short text.
 *
 * ![Badge image](https://developer.android.com/images/reference/androidx/compose/material3/badge.png)
 *
 * See [BadgedBox] for a top level layout that will properly place the badge relative to content
 * such as text or an icon.
 *
 * @param modifier the [Modifier] to be applied to this badge
 * @param containerColor the color used for the background of this badge
 * @param contentColor the preferred color for content inside this badge. Defaults to either the
 * matching content color for [containerColor], or to the current [LocalContentColor] if
 * [containerColor] is not a color from the theme.
 * @param content optional content to be rendered inside this badge
 */
@ExperimentalMaterial3Api
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    containerColor: Color = BadgeDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    val size = if (content != null) BadgeTokens.LargeSize else BadgeTokens.Size
    val shape = if (content != null) {
        BadgeTokens.LargeShape
    } else {
        BadgeTokens.Shape
    }

    // Draw badge container.
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = size, minHeight = size)
            .background(
                color = containerColor,
                shape = shape
            )
            .clip(shape)
            .then(
                if (content != null)
                    Modifier.padding(horizontal = BadgeWithContentHorizontalPadding) else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (content != null) {
            // Not using Surface composable because it blocks touch propagation behind it.
            CompositionLocalProvider(
                LocalContentColor provides contentColor
            ) {

                val style =
                    LargeLabelTextStyle.copy(platformStyle = PlatformTextStyle(includeFontPadding = false))

                ProvideTextStyle(
                    value = style,
                    content = { content() }
                )
            }
        }
    }
}

/** Default values used for [Badge] implementations. */
@ExperimentalMaterial3Api
object BadgeDefaults {
    /** Default container color for a badge. */
    val containerColor: Color @Composable get() = OddOneOutTheme.colorScheme.accent.color
}

/*@VisibleForTesting*/
// Leading and trailing text padding when a badge is displaying text that is too long to fit in
// a circular badge, e.g. if badge number is greater than 9.
internal val BadgeWithContentHorizontalPadding = 4.dp

/*@VisibleForTesting*/
// Horizontally align start/end of text badge 4dp from the top end corner of its anchor
internal val BadgeWithContentHorizontalOffset = -4.dp
internal val BadgeWithContentVerticalOffset = -4.dp

/*@VisibleForTesting*/
// Horizontally align start/end of icon only badge 0.dp from the end/start edge of anchor
internal val BadgeOffset = 0.dp

internal object BadgeTokens {
    val LargeLabelTextStyle = OddOneOutTheme.typography.Label.L600.style
    val LargeShape = Radii.Round.shape
    val LargeSize = 16.0.dp
    val Shape = Radii.Round.shape
    val Size = 6.0.dp
}

@Preview(widthDp = 300, heightDp = 300)
@Composable
private fun PreviewBadgedBox() {
    PreviewContent(showBackground = true) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val radius = Radii.Card

            BadgedBox(
                badge = {
                    CircularIcon(
                        icon = SpyfallIcon.Close(""),
                        iconSize = IconSize.Small,
                        backgroundColor = OddOneOutTheme.colorScheme.onBackground,
                        contentColor = OddOneOutTheme.colorScheme.background,
                        padding = Spacing.S100,
                        elevation = Elevation.Fixed
                    )
                },
                contentRadius = radius,
                content = {
                    Surface(
                        radius = radius,
                        color = OddOneOutTheme.colorScheme.accent,
                        contentColor = OddOneOutTheme.colorScheme.onBackground,
                        contentPadding = PaddingValues(Spacing.S800),
                    ) {
                        Text(text = "Example")
                    }
                }
            )
        }
    }
}