package com.dangerfield.features.settings.internal.referral

import androidx.lifecycle.viewModelScope
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.ui.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val getMeReferralCode: GetMeReferralCode,
    private val referralCodeLength: ReferralCodeLength
) : SEAViewModel<ReferralViewModel.State, ReferralViewModel.Event, ReferralViewModel.Action>() {

    init {
        viewModelScope.launch {
            val meReferralCode = getMeReferralCode.invoke()
            updateState {
                it.copy(
                    meReferralCode = meReferralCode.code,
                    meRedemptionStatus = meReferralCode.redemptionStatus,
                    isLoading = false
                )
            }
        }
    }

    override val initialState = State()

    override suspend fun handleAction(action: Action) {
        when (action) {
            is Action.Redeem -> {
                val referralCode = action.referralCode
                if (referralCode == state.value.meReferralCode) {
                    updateState {
                        it.copy(
                            referralCodeFieldState = FieldState.Error("You can't redeem your own referral code"),
                            isFormValid = false
                        )
                    }
                } else {
                    updateState { it.copy(isLoading = true) }
                    // call redeem code use case
                    // check for this user and if theyve already redeemed the max ammount
                    // check if this code has already been redeemed at all
                    // otherwise add to database
                    delay(3000)

                    updateState {
                        it.copy(
                            isLoading = false,
                            isFormValid = false,
                            referralCodeFieldState = FieldState.Idle("", message = "Referral code redeemed!"),
                        )
                    }
                }
            }

            is Action.UpdateReferralCodeField -> {
                val referralCode = action.referralCode
                updateState {
                    val isFieldValid = referralCode.length == referralCodeLength.value
                    it.copy(
                        referralCodeFieldState = if (isFieldValid) FieldState.Valid(referralCode) else FieldState.Invalid(
                            referralCode,
                            "Referral code must be ${referralCodeLength.value} characters"
                        ),
                        isFormValid = isFieldValid
                    )
                }
            }
        }
    }


    data class State(
        val meReferralCode: String = "",
        val meRedemptionStatus: RedemptionStatus? = null,
        val referralCodeFieldState: FieldState<String> = FieldState.Idle(""),
        val featureMessage: String? = null,
        val isFormValid: Boolean = false,
        val maxRedemptions: Int = 0,
        val isLoading: Boolean = true
    )

    sealed class Event {
        data class SubmissionError(val message: String) : Event()
    }

    sealed class Action {
        data class Redeem(val referralCode: String) : Action()
        data class UpdateReferralCodeField(val referralCode: String) : Action()
    }
}