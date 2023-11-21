package com.dangerfield.libraries.common.internal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.AppState
import spyfallx.core.ApplicationStateRepository
import javax.inject.Inject

@AutoBind
class ApplicationStateRepositoryImpl @Inject constructor(): ApplicationStateRepository {

    private val appStateFlow = MutableStateFlow(AppState.FOREGROUND)

    override fun getApplicationStateFlow(): StateFlow<AppState> = appStateFlow

    override fun onAppStart() {
        appStateFlow.tryEmit(AppState.FOREGROUND)
    }

    override fun onAppStop() {
        appStateFlow.tryEmit(AppState.BACKGROUND)
    }
}
