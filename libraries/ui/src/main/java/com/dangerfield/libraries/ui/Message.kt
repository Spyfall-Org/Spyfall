package com.dangerfield.libraries.ui

import oddoneout.core.Message
import oddoneout.core.SnackBarPresenter

fun showDeveloperMessage(developerMessage: Message) {
    SnackBarPresenter.showDebugMessage(developerMessage)
}

fun showDeveloperMessage(autoDismiss: Boolean = true, lazyMessage: () -> String) {
    SnackBarPresenter.showDebugMessage(Message(lazyMessage(), autoDismiss))
}

fun showMessage(message: Message) {
    SnackBarPresenter.showMessage(message)
}

fun showMessage(autoDismiss: Boolean = true, lazyMessage: () -> String) {
    SnackBarPresenter.showMessage(Message(lazyMessage(), autoDismiss))
}
