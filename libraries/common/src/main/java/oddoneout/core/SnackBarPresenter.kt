package oddoneout.core

import kotlinx.coroutines.channels.Channel
import spyfallx.core.common.BuildConfig

object SnackBarPresenter {

    val messages: Channel<Message> = Channel(Channel.UNLIMITED)

    fun showDebugMessage(message: Message) {
        if (BuildConfig.DEBUG) {
            messages.trySend(message.copy(isDebug = true))
        }
    }

    fun showMessage(message: Message) {
        messages.trySend(message)
    }

    fun showMessage(
        message: String,
        title: String? = null,
        autoDismiss: Boolean = true,
        action: (() -> Unit)? = null,
        actionLabel: String? = null
    ) {
        messages.trySend(
            Message(
                title = title,
                message = message,
                autoDismiss = autoDismiss,
                action = action,
                actionLabel = actionLabel
            )
        )
    }
}

data class Message(
    val message: String,
    val autoDismiss: Boolean,
    val isDebug : Boolean = false,
    val title: String? = null,
    val action: (() -> Unit)? = null,
    val actionLabel: String? = null
)
