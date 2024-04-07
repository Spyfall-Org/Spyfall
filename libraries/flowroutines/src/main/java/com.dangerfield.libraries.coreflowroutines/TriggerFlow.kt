package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * A flow that can be used as a trigger. Initially emits Unit, and then emits Unit every time [pull] is called.
 */
class TriggerFlow(
    private val triggerFlow: MutableStateFlow<Int> = MutableStateFlow(0),
    private val unitFlow: Flow<Unit> = triggerFlow.map { Unit }
) : Flow<Unit> by unitFlow {

    /**
     * Call this to trigger the flow to emit a new value
     */
    fun pull() {
        triggerFlow.update { it + 1 }
    }
}