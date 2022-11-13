package spyfallx.coreui

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewScopedReference<T>(
    private val fragment: Fragment,
    private val factory: (View) -> T
) : ReadOnlyProperty<Any, T?> {

    private var instance: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observeForever {
            fragment.viewLifecycleOwner.lifecycle.addObserver(viewLifeCycleObserver)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T? = instance

    private val viewLifeCycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            instance = factory(fragment.requireView())
        }

        override fun onDestroy(owner: LifecycleOwner) {
            instance = null
        }
    }
}

fun <T> Fragment.viewScoped(factory: (View) -> T): ViewScopedReference<T> {
    return ViewScopedReference(this, factory)
}
