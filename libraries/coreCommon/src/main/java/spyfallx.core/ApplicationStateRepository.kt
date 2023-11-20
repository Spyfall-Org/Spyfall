package spyfallx.core

import kotlinx.coroutines.flow.StateFlow

interface ApplicationStateRepository {
    fun getApplicationStateFlow(): StateFlow<AppState>
    fun onAppStart()
    fun onAppStop()
}

enum class AppState {
    FOREGROUND,
    BACKGROUND
}
