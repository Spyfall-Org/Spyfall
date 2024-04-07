package com.dangerfield.features.settings.internal.contactus

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.Action
import com.dangerfield.features.settings.internal.contactus.ContactUsViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.oddoneoout.features.settings.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import oddoneout.core.eitherWay
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val sendContactForm: SendContactForm,
    private val dictionary: Dictionary,
    savedStateHandle: SavedStateHandle
) : SEAViewModel<State, Nothing, Action>(savedStateHandle) {

    override fun initialState() = State(
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
        with(action) {
            when (this) {
                is Action.Submit -> handleSubmit()
                is Action.UpdateContactReason -> updateContactReason()
                is Action.UpdateEmail -> updateEmail()
                is Action.UpdateMessage -> updateMessage()
                is Action.UpdateName -> updateName()
                is Action.DismissSomethingWentWrong -> updateState { it.copy(didSubmitFail = false) }
            }
        }
    }

    private suspend fun Action.UpdateContactReason.updateContactReason() {
        updateState {
            it.copy(contactReasonState = FieldState.Valid(contactReason)).withFormValidation()
        }
    }

    private suspend fun Action.UpdateEmail.updateEmail() {
        updateState {
            // any typing will clear errors and show valid, the validation is debounced
            it.copy(emailFieldState = FieldState.Valid(email)).withFormValidation()
        }

        debounceUpdateState(1000L) {
            val state = if (email.isNotEmpty() && email.isEmailFormat()) {
                FieldState.Valid(email)
            } else {
                FieldState.Invalid(
                    email,
                    dictionary.getString(R.string.contactUs_invalidEmail_text)
                )
            }

            it.copy(emailFieldState = state).withFormValidation()
        }
    }

    private suspend fun Action.UpdateMessage.updateMessage() {
        updateState {
            val state = if (message.isNotEmpty()) {
                FieldState.Valid(message)
            } else {
                FieldState.Invalid(
                    message,
                    dictionary.getString(R.string.contactUs_invalidMessage_text)
                )
            }
            it.copy(messageFieldState = state).withFormValidation()
        }
    }

    private suspend fun Action.UpdateName.updateName(
    ) {
        updateState {
            val state = if (name.isNotEmpty()) {
                FieldState.Valid(name)
            } else {
                FieldState.Invalid(
                    name,
                    dictionary.getString(R.string.contactUs_invalidName_text)
                )
            }
            it.copy(nameFieldState = state).withFormValidation()
        }
    }

    private suspend fun Action.handleSubmit() {
        if (!stateFlow.value.isFormValid) return
        updateState { it.copy(isLoadingSubmit = true) }
        val state = stateFlow.value

        sendContactForm.invoke(
            name = state.nameFieldState.value.orEmpty(),
            email =  state.emailFieldState.value.orEmpty(),
            message =  state.messageFieldState.value.orEmpty(),
            contactReason = state.contactReasonState.value ?: ContactReason.Other
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

    private suspend fun handleSubmit(
        update: ((State) -> State) -> Unit
    ) {
        if (!stateFlow.value.isFormValid) return
        update { it.copy(isLoadingSubmit = true) }
        val state = stateFlow.value

        sendContactForm.invoke(
            name = state.nameFieldState.value.orEmpty(),
            email =  state.emailFieldState.value.orEmpty(),
            message =  state.messageFieldState.value.orEmpty(),
            contactReason = state.contactReasonState.value ?: ContactReason.Other
        )
            .onSuccess {
                update { it.copy(wasFormSuccessfullySubmitted = true) }
            }
            .onFailure {
                update { it.copy(didSubmitFail = true) }
            }
            .eitherWay {
                update { it.copy(isLoadingSubmit = false) }
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

    @Parcelize
    data class State(
        val isLoadingSubmit: Boolean,
        val contactReasonState: FieldState<ContactReason?>,
        val nameFieldState: FieldState<String>,
        val emailFieldState: FieldState<String>,
        val messageFieldState: FieldState<String>,
        val isFormValid: Boolean,
        val didSubmitFail: Boolean,
        val wasFormSuccessfullySubmitted: Boolean,
    ) : Parcelable

    sealed class Action {
        data class UpdateEmail(val email: String) : Action()
        data class UpdateName(val name: String) : Action()
        data class UpdateMessage(val message: String) : Action()
        data class UpdateContactReason(val contactReason: ContactReason) : Action()
        data object DismissSomethingWentWrong : Action()
        data object Submit : Action()
    }
}