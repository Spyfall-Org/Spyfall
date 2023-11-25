package spyfallx.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun PaddingValues.calculateHorizontalPadding(): Dp =
    calculateLeftPadding(LayoutDirection.Ltr) + calculateRightPadding(LayoutDirection.Ltr)

fun PaddingValues.calculateVerticalPadding(): Dp =
    calculateTopPadding() + calculateBottomPadding()

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues = object : PaddingValues {

    override fun calculateBottomPadding(): Dp = this@plus.calculateBottomPadding() + other.calculateBottomPadding()

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@plus.calculateLeftPadding(layoutDirection) + other.calculateLeftPadding(layoutDirection)

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@plus.calculateRightPadding(layoutDirection) + other.calculateRightPadding(layoutDirection)

    override fun calculateTopPadding(): Dp = this@plus.calculateTopPadding() + other.calculateTopPadding()
}
