package com.dangerfield.libraries.corecommon.internal

import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.ApplicationState
import oddoneout.core.ApplicationStateRepository
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class ApplicationStateRepositoryImpl @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope
): ApplicationStateRepository {

    private val appStateFlow = MutableStateFlow(ApplicationState.Foregrounded)

    override fun applicationState(): StateFlow<ApplicationState> = appStateFlow

    override fun onAppStart() {
        applicationScope.launch {
            appStateFlow.emit(ApplicationState.Foregrounded)
        }
    }

    override fun onAppStop() {
        applicationScope.launch {
            appStateFlow.emit(ApplicationState.Backgrounded)
        }
    }

    override fun onAppDestroyed() {
        applicationScope.launch {
            appStateFlow.emit(ApplicationState.Destroyed)
        }    }
}
