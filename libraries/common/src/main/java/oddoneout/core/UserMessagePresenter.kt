package oddoneout.core

import kotlinx.coroutines.channels.Channel
import spyfallx.core.common.BuildConfig

object UserMessagePresenter  {

    val messages: Channel<Message> = Channel<Message>()

    fun showDeveloperMessage(messageConfig: Message) {
        if (BuildConfig.DEBUG) {
            messages.trySend(messageConfig.copy(message = "$DebugSnackMessageToken${messageConfig.message}"))
        }
    }

    fun showMessage(messageConfig: Message) {
        messages.trySend(messageConfig)
    }
}

const val DebugSnackMessageToken = "{DEBUG}"

data class Message(val message: String, val autoDismiss: Boolean)
