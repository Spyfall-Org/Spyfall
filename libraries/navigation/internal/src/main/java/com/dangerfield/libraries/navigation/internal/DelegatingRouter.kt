package com.dangerfield.libraries.navigation.internal

import androidx.compose.runtime.Stable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.observeWithLifecycle
import com.dangerfield.libraries.navigation.Route
import com.dangerfield.libraries.navigation.Router
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Stable
@Singleton
class DelegatingRouter @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
) : Router {

    private var delegate: Router? = null
    override val currentRouteName: String? get() = delegate?.currentRouteName
    private val navigationRequests = Channel<Router.() -> Unit>(Channel.UNLIMITED)

    fun setDelegate(router: Router, lifecycle: Lifecycle) {
        delegate = router

        applicationScope.launch {
            navigationRequests.receiveAsFlow().observeWithLifecycle(lifecycle) { action ->
                action.invoke(router)
            }
        }
    }

    override fun navigate(filledRoute: Route.Filled) {
        // use continuations or something to wait until the delegate has been set
        navigationRequests.trySend { navigate(filledRoute) }
    }

    override fun goBack() {
        navigationRequests.trySend { goBack() }
    }

    override fun openWebLink(url: String, openInApp: Boolean) {
        navigationRequests.trySend { openWebLink(url, openInApp) }
    }

    override fun popBackTo(route: Route.Template, inclusive: Boolean) {
        navigationRequests.trySend { popBackTo(route, inclusive) }
    }

    override fun dismissSheet(sheetState: BottomSheetState) {
        navigationRequests.trySend { dismissSheet(sheetState) }
    }

    override fun ifStillOn(backStackEntry: NavBackStackEntry, action: Router.() -> Unit) {
        navigationRequests.trySend { ifStillOn(backStackEntry, action) }
    }

    override fun getBackStackEntry(route: Route.Template): NavBackStackEntry {
        return delegate?.getBackStackEntry(route) ?: throw IllegalStateException("Delegate not set")
    }
}