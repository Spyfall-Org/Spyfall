package spyfallx.coreui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import javax.inject.Provider

class ViewModelUtils {

    companion object {
        /**
         * Get a [ViewModel] of type <T>, scoped to the provided [ViewModelStoreOwner].
         *
         * @param viewModelStoreOwner Owner used to create a [ViewModelProvider]
         * @param viewModelClass Class of the [ViewModel] .
         * @param provider       A Way to instantiate a new instances if viewModelStoreOwner has none
         * @param <T>            Type of the [ViewModel].
         * @return A new or existing instance of the given [ViewModel] class.
         **/
        fun <T : ViewModel> getViewModel(
            viewModelStoreOwner: ViewModelStoreOwner,
            viewModelClass: Class<T>,
            provider: Provider<T>
        ): T {
            return ViewModelProvider(viewModelStoreOwner, object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = provider.get() as T
            })[viewModelClass]
        }
    }
}