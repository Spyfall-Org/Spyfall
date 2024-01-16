package com.dangerfield.libraries.ui

import oddoneout.core.Message
import oddoneout.core.UserMessagePresenter

fun showDeveloperMessage(developerMessage: Message) {
    UserMessagePresenter.showDeveloperMessage(developerMessage)
}

fun showDeveloperMessage(autoDismiss: Boolean = true, lazyMessage: () -> String) {
    UserMessagePresenter.showDeveloperMessage(Message(lazyMessage(), autoDismiss))
}

fun showMessage(message: Message) {
    UserMessagePresenter.showMessage(message)
}

fun showMessage(autoDismiss: Boolean = true, lazyMessage: () -> String) {
    UserMessagePresenter.showMessage(Message(lazyMessage(), autoDismiss))
}
