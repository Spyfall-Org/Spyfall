package spyfallx.coreui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * Convenience method for creating a [FragmentViewBinding] with the bind method
 * ex: private val binding = viewBinding(MyFragmentBinding::bind)
 */
fun <V : ViewBinding> Fragment.viewBinding(factory: (View) -> V) =
    FragmentViewBinding(this, factory)

/**
 * Convenience method for creating a [FragmentViewBinding] with the inflate method
 * ex: private val binding = viewBinding(MyFragmentBinding::inflate)
 */
fun <V : ViewBinding> Fragment.viewBinding(factory: (LayoutInflater, ViewGroup?, Boolean) -> V) =
    FragmentViewBinding(this, factory)

/**
 * Helper class for managing a Fragment's [ViewBinding] by allowing late inflation or binding and
 * setting the reference to null when views are destroyed.
 *
 * See (https://developer.android.com/topic/libraries/view-binding#fragments)
 */
class FragmentViewBinding<V : ViewBinding> : DefaultLifecycleObserver {

    private val fragment: Fragment
    private val bindFactory: ((View) -> V)?
    private val inflateFactory: ((LayoutInflater, ViewGroup?, Boolean) -> V)?
    private val fragmentName get() = fragment.javaClass.name

    constructor(fragment: Fragment, factory: (LayoutInflater, ViewGroup?, Boolean) -> V) {
        this.fragment = fragment
        inflateFactory = factory
        bindFactory = null
    }

    constructor(fragment: Fragment, factory: (View) -> V) {
        this.fragment = fragment
        bindFactory = factory
        inflateFactory = null
    }

    /**
     * Convenience method for accessing [ViewBinding.getRoot]
     */
    val root get() = view.root

    /**
     * Provides access to the [ViewBinding] managed by this [FragmentViewBinding]
     */
    val view: V
        get() {
            check(isInitialized) { "($fragmentName) Should not attempt to get bindings after views are destroyed" }
            return checkNotNull(binding) { "($fragmentName) Missing call to $factoryMethod" }
        }

    /**
     * Provides access to the [ViewBinding] managed by this [FragmentViewBinding], or null if not initialized
     * This method should only be used if the restrictions enforced by [view] can't be guaranteed
     */
    val viewOrNull get() = binding?.takeIf { isInitialized }

    /**
     * Determines if the fragment lifecycle state is at least [Lifecycle.State.INITIALIZED]
     */
    private val isInitialized get() = lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)

    private val lifecycle
        get() = runCatching { fragment.viewLifecycleOwner.lifecycle }
            .recover { throw IllegalStateException("($fragmentName) View lifecycle is null", it) }
            .getOrThrow()

    private val factoryMethod get() = "FragmentViewBinding.${if (inflateFactory != null) "inflate" else "bind"}()"

    private var binding: V? = null

    /**
     * Binds the [ViewBinding] managed by this [FragmentViewBinding] using the provided factory
     * ex: binding.bind(view)
     */
    fun bind(view: View): V {
        checkNotNull(bindFactory) { "($fragmentName) FragmentViewBinding was not created with a bind method" }
        return create { bindFactory.invoke(view) }
    }

    /**
     * Inflates the [ViewBinding] managed by this [FragmentViewBinding] using the provided factory
     * ex: binding.inflate(inflater, container)
     */
    fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean = false): V {
        checkNotNull(inflateFactory) { "($fragmentName) FragmentViewBinding was not created with an inflate method" }
        return create { inflateFactory.invoke(inflater, container, attachToRoot) }
    }

    private inline fun create(factory: () -> V): V {
        check(binding == null) { "($fragmentName) $factoryMethod has already been called" }
        lifecycle.addObserver(this)
        return factory().also { binding = it }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        binding = null
    }
}
