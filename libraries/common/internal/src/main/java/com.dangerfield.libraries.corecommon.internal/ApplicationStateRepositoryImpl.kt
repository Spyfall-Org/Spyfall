package com.dangerfield.libraries.corecommon.internal

import android.util.Log
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.AppState
import spyfallx.core.ApplicationStateRepository
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class ApplicationStateRepositoryImpl @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope
): ApplicationStateRepository {

    private val appStateFlow = MutableStateFlow(AppState.FOREGROUND)

    override fun getApplicationStateFlow(): StateFlow<AppState> = appStateFlow

    override fun onAppStart() {
        applicationScope.launch {
            Log.d("Elijah", "about to emit foreground")
            appStateFlow.emit(AppState.FOREGROUND)
        }
    }

    override fun onAppStop() {
        applicationScope.launch {
            Log.d("Elijah", "about to emit background")
            appStateFlow.emit(AppState.BACKGROUND)
        }
    }
}
