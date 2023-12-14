package spyfallx.core

import kotlinx.coroutines.channels.Channel
import spyfallx.core.common.BuildConfig

object DeveloperMessagePresenter  {

    val messages: Channel<DeveloperMessage> = Channel<DeveloperMessage>()

    fun showDeveloperMessage(messageConfig: DeveloperMessage) {
        if (BuildConfig.DEBUG) {
            messages.trySend(messageConfig.copy(message = "$DebugSnackMessageToken${messageConfig.message}"))
        }
    }
}

const val DebugSnackMessageToken = "{DEBUG}"

data class DeveloperMessage(val message: String, val autoDismiss: Boolean)
