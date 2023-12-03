package com.dangerfield.libraries.ui.tooltip

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dangerfield.libraries.ui.tooltip.TooltipDuration
import com.dangerfield.libraries.ui.tooltip.TooltipManager
import kotlinx.coroutines.withTimeoutOrNull

/**
 * The state that is associated with an instance of a tooltip.
 * Each instance of tooltips should have its own [TooltipState].
 */
@Stable
class TooltipState {
    /**
     * [Boolean] that will be used to update the visibility
     * state of the associated tooltip.
     */
    var isVisible: Boolean by mutableStateOf(false)
        internal set

    /**
     * Show the tooltip associated with the current [TooltipState].
     * It will dismiss after a short duration. When this method is called,
     * all of the other tooltips currently being shown will dismiss.
     */
    suspend fun show() {
        withTimeoutOrNull(TooltipDuration) {
            TooltipManager.showForever(state = this@TooltipState)
        }
    }

    /**
     * Dismiss the tooltip associated with this [TooltipState] if it's currently being shown.
     */
    suspend fun dismiss() {
        TooltipManager.dismissCurrentTooltip(this)
    }
}
