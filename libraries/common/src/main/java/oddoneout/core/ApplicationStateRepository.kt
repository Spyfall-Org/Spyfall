package oddoneout.core

import kotlinx.coroutines.flow.StateFlow

interface ApplicationStateRepository {
    fun foregroundStateFlow(): StateFlow<ForegroundState>
    fun onAppStart()
    fun onAppStop()
}

enum class ForegroundState {
    FOREGROUND,
    BACKGROUND
}
