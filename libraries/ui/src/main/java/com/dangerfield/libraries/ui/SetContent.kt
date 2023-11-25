package spyfallx.ui

import android.view.View
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.customview.poolingcontainer.PoolingContainerListener
import androidx.customview.poolingcontainer.addPoolingContainerListener
import androidx.customview.poolingcontainer.isWithinPoolingContainer
import androidx.customview.poolingcontainer.removePoolingContainerListener
import androidx.lifecycle.findViewTreeLifecycleOwner

//fun Context.setContent(
//    accentColor: ColorPrimitive = ColorPrimitive.CherryPop700,
//    viewCompositionStrategy: ViewCompositionStrategy = ViewCompositionStrategy.Dynamic,
//    content: @Composable () -> Unit,
//) = ComposeView(this).apply {
//    setViewCompositionStrategy(viewCompositionStrategy)
//    setContent {
//        SpyfallTheme(
//            isDarkMode = isSystemInDarkTheme(),
//            accentColor = accentColor
//        ) {
//            content()
//        }
//    }
//}

/**
 * A [ViewCompositionStrategy] that tries to determine the best strategy dynamically.
 *
 * The purpose of this strategy is to serve as a better default value since it get guess what needs to be done.
 *
 * 1. If the view is in a pooling container (like a `RecyclerView`), then composition is disposed when released from
 *    the pool.
 * 2. If there is a ViewTreeLifecycleOwner present, then it disposes when the lifecycle is destroyed.
 * 3. Otherwise it disposes when the view is detached from the window.
 */
val ViewCompositionStrategy.Companion.Dynamic: ViewCompositionStrategy
    get() = DynamicViewCompositionStrategy

private object DynamicViewCompositionStrategy : ViewCompositionStrategy {
    override fun installFor(view: AbstractComposeView): () -> Unit {
        var disposer: () -> Unit

        fun onAttached(): () -> Unit {
            if (view.isWithinPoolingContainer) {
                val listener = PoolingContainerListener { view.disposeComposition() }
                view.addPoolingContainerListener(listener)
                return { view.removePoolingContainerListener(listener) }
            }

            val lifecycleOwner = view.findViewTreeLifecycleOwner()

            return if (lifecycleOwner != null) {
                 ViewCompositionStrategy.DisposeOnLifecycleDestroyed(lifecycleOwner).installFor(view)
            } else ViewCompositionStrategy.DisposeOnDetachedFromWindow.installFor(view)
        }

        if (view.isAttachedToWindow) {
            disposer = onAttached()
        } else {
            val listener = object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.removeOnAttachStateChangeListener(this)
                    disposer = onAttached()
                }

                override fun onViewDetachedFromWindow(v: View) {}
            }
            disposer = { view.removeOnAttachStateChangeListener(listener) }
            view.addOnAttachStateChangeListener(listener)
        }
        return disposer
    }
}
