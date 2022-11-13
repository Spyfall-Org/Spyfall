package spyfallx.coreui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.collectWhileStarted(lifecycleOwner: LifecycleOwner, block: (T) -> Unit) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            collectLatest {
                block(it)
            }
        }
    }
}