package com.dangerfield.features.settings.internal.contactus

import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.ui.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val sendContactForm: SendContactForm
) : SEAViewModel<State, Nothing, Action>() {

    override val initialState = State(
        isLoadingSubmit = false,
        contactReasonState = FieldState.Idle(null),
        nameFieldState = FieldState.Idle(""),
        emailFieldState = FieldState.Idle(""),
        messageFieldState = FieldState.Idle(""),
        isFormValid = false,
        didSubmitFail = false,
        wasFormSuccessfullySubmitted = false
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.Submit -> handleSubmit()
            is Action.UpdateContactReason -> updateContactReason(action.contactReason)
            is Action.UpdateEmail -> updateEmail(action.email)
            is Action.UpdateMessage -> updateMessage(action.message)
            is Action.UpdateName -> updateName(action.name)
            is Action.DismissSomethingWentWrong -> updateState { it.copy(didSubmitFail = false) }
        }
    }

    private suspend fun updateContactReason(contactReason: ContactReason) {
        updateState {
            it.copy(contactReasonState = FieldState.Valid(contactReason)).withFormValidation()
        }
    }

    private suspend fun updateEmail(email: String) {
        updateState {
            val state = if (email.isNotEmpty() && email.isEmailFormat()) {
                FieldState.Valid(email)
            } else {
                FieldState.Invalid(
                    email,
                    "Please enter a valid email address"
                )
            }
            it.copy(emailFieldState = state).withFormValidation()
        }
    }

    private suspend fun updateMessage(message: String) {
        updateState {
            val state = if (message.isNotEmpty()) {
                FieldState.Valid(message)
            } else {
                FieldState.Invalid(
                    message,
                    "Please enter a message"
                )
            }
            it.copy(messageFieldState = state).withFormValidation()
        }
    }

    private suspend fun updateName(name: String) {
        updateState {
            val state = if (name.isNotEmpty()) {
                FieldState.Valid(name)
            } else {
                FieldState.Invalid(
                    name,
                    "Please enter a name"
                )
            }
            it.copy(nameFieldState = state).withFormValidation()
        }
    }

    private suspend fun handleSubmit() {
        updateState { it.copy(isLoadingSubmit = true) }
        val state = state.value

        sendContactForm.invoke(
            name = state.nameFieldState.backingValue.orEmpty(),
            email =  state.emailFieldState.backingValue.orEmpty(),
            message =  state.messageFieldState.backingValue.orEmpty(),
            contactReason = state.contactReasonState.backingValue ?: ContactReason.None
        )
            .onSuccess {
                updateState { it.copy(wasFormSuccessfullySubmitted = true) }
            }
            .onFailure {
                updateState { it.copy(didSubmitFail = true) }
            }
            .eitherWay {
                updateState { it.copy(isLoadingSubmit = false) }
            }
    }

    private fun String.isEmailFormat(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun State.withFormValidation(): State {
        val isFormValid = contactReasonState is FieldState.Valid &&
                nameFieldState is FieldState.Valid &&
                emailFieldState is FieldState.Valid &&
                messageFieldState is FieldState.Valid
        return copy(isFormValid = isFormValid)
    }

    data class State(
        val isLoadingSubmit: Boolean,
        val contactReasonState: FieldState<ContactReason?>,
        val nameFieldState: FieldState<String>,
        val emailFieldState: FieldState<String>,
        val messageFieldState: FieldState<String>,
        val isFormValid: Boolean,
        val didSubmitFail: Boolean,
        val wasFormSuccessfullySubmitted: Boolean,
    )

    sealed class Action {
        data class UpdateEmail(val email: String) : Action()
        data class UpdateName(val name: String) : Action()
        data class UpdateMessage(val message: String) : Action()
        data class UpdateContactReason(val contactReason: ContactReason) : Action()
        data object DismissSomethingWentWrong : Action()
        data object Submit : Action()
    }
}