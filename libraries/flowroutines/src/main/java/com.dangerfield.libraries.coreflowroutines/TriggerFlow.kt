package com.dangerfield.libraries.coreflowroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TriggerFlow(
    private val triggerFlow: MutableStateFlow<Int> = MutableStateFlow(0),
    private val unitFlow: Flow<Unit> = triggerFlow.map { Unit }
) : Flow<Unit> by unitFlow {

    fun pull() {
        triggerFlow.update { it + 1 }
    }
}