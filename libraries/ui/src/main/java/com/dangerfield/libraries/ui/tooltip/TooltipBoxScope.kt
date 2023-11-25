package co.hinge.common.design.system.components.tooltip

import androidx.compose.ui.Modifier

interface TooltipBoxScope {
    /**
     * [Modifier] that should be applied to the anchor composable when showing the tooltip
     * after long pressing the anchor composable is desired. It appends a long click to
     * the composable that this modifier is chained with.
     */
    fun Modifier.tooltipAnchor(): Modifier
}
