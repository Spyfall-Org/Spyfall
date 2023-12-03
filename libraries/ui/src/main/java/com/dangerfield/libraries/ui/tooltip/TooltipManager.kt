package com.dangerfield.libraries.ui.tooltip

import androidx.compose.foundation.*
import androidx.compose.runtime.Stable
import kotlinx.coroutines.awaitCancellation
import kotlin.time.Duration.Companion.seconds

@Stable
internal object TooltipManager {
    private val mutatorMutex: MutatorMutex = MutatorMutex()
    private var mutexOwner: TooltipState? = null

    /** Shows the tooltip associated with [TooltipState], it dismisses any tooltip currently being shown. */
    suspend fun showForever(state: TooltipState) {
        mutatorMutex.mutate(MutatePriority.Default) {
            try {
                mutexOwner = state
                state.isVisible = true
                awaitCancellation()
            } finally {
                mutexOwner = null
                // timeout or cancellation has occurred
                // and we close out the current tooltip.
                state.isVisible = false
            }
        }
    }

    /**
     * Dismisses the tooltip currently
     * being shown by freeing up the lock.
     */
    suspend fun dismissCurrentTooltip(
        state: TooltipState,
    ) {
        if (state == mutexOwner) {
            mutatorMutex.mutate(MutatePriority.UserInput) {
                /* Do nothing, we're just freeing up the mutex */
            }
        }
    }
}

internal val TooltipDuration = 1.5.seconds
