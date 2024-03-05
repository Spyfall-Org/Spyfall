package oddoneout.core

import kotlinx.coroutines.flow.StateFlow

interface ApplicationStateRepository {
    fun applicationState(): StateFlow<ApplicationState>
    fun onAppStart()
    fun onAppStop()
    fun onAppDestroyed()
}

enum class ApplicationState {
    Foregrounded,
    Backgrounded,
    Destroyed
}
