package com.dangerfield.spyfall.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.forcedupdate.IsAppUpdateRequired
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import spyfallx.core.logOnError
import com.dangerfield.libraries.ui.color.ColorPrimitive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import spyfallx.core.Try
import spyfallx.core.failFast
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isUpdateRequired: IsAppUpdateRequired,
    private val ensureAppConfigLoaded: EnsureAppConfigLoaded,
    private val ensureSessionLoaded: EnsureSessionLoaded
) : ViewModel() {

    val state: StateFlow<State> = flow {
        tryWithTimeout(10.seconds) {
           startUpTasks.awaitAll().failFast()
        }
            .logOnError()
            .onFailure { emit(State.Error) }
            .onSuccess {
                emit(
                    State.Loaded(
                        isUpdateRequired = isUpdateRequired(),
                        accentColor = ColorPrimitive.CherryPop700,
                    )
                )
            }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue = State.Loading
        )

    private val CoroutineScope.startUpTasks: List<Deferred<Try<Unit>>>
        get() = listOf(
            async { ensureSessionLoaded() },
            async { ensureAppConfigLoaded() }
        )

    sealed class State {
        data class Loaded(
            val isUpdateRequired: Boolean,
            val accentColor: ColorPrimitive
        ) : State()

        data object Loading : State()

        data object Error : State()
    }
}
