package com.dangerfield.core.ui
import android.view.View
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.fragment.app.Fragment

class ViewScopedProperty<T: Any>(
    view: View,
    private val factory: (View) -> T
) {

    var instance: T? = null
    private set

    init {
        view.doOnAttach {
            instance = factory(it)
        }

        view.doOnDetach {
            instance = null
        }
    }
}

fun <T: Any> Fragment.viewScoped(factory: (View) -> T) : ViewScopedProperty<T> =
    ViewScopedProperty(requireView(), factory)


fun <T: Any> View.viewScoped(factory: (View) -> T): ViewScopedProperty<T> =
    ViewScopedProperty(this, factory)