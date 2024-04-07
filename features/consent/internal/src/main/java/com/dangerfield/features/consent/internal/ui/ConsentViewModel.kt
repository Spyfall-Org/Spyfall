package com.dangerfield.features.consent.internal.ui

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.ConsentStatus.ConsentDenied
import com.dangerfield.features.consent.ConsentStatus.ConsentGiven
import com.dangerfield.features.consent.ConsentStatus.ConsentNeeded
import com.dangerfield.features.consent.ConsentStatus.ConsentNotNeeded
import com.dangerfield.features.consent.ConsentStatus.Unknown
import com.dangerfield.features.consent.internal.GDRPConsentManager
import com.dangerfield.features.consent.internal.InHouseConsentManager
import com.dangerfield.features.consent.internal.ui.ConsentViewModel.Action
import com.dangerfield.features.consent.internal.ui.ConsentViewModel.State
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentViewModel @Inject constructor(
    private val gdrpConsentManager: GDRPConsentManager,
    private val inHouseConsentManager: InHouseConsentManager,
    savedStateHandle: SavedStateHandle
): SEAViewModel<State, Unit, Action>(savedStateHandle) {

    override fun initialState() = State(
        shouldShowInHouseConsentMessage = false,
        shouldShowGDRPConsentMessage = false,
        isLoading = true
    )

    override suspend fun handleAction(action: Action) {
        when(action) {
            is Action.LoadConsentStatus -> action.observeConsentStatus()
            is Action.UpdateInHouseConsentStatus -> inHouseConsentManager.updateConsentStatus(action.status)
            is Action.ForceGDRPConsent -> gdrpConsentManager.updateConsentStatus(
                activity = action.activity,
                status = action.status
            )
        }
    }

    private suspend fun  Action.LoadConsentStatus.observeConsentStatus() = viewModelScope.launch {
        combine(
            gdrpConsentManager.getConsentStatusFlow(activity),
            inHouseConsentManager.getConsentStatusFlow()
        ) { gdrpConsentStatus, inHouseConsentStatus ->
            val isGDRPConsentNeeded = gdrpConsentStatus == ConsentNeeded || gdrpConsentStatus == ConsentDenied
            State(
                shouldShowGDRPConsentMessage = isGDRPConsentNeeded,
                shouldShowInHouseConsentMessage = shouldShowInHouseConsentMessage(
                    gdrpConsentStatus,
                    inHouseConsentStatus
                ),
                isLoading = false
            )
        }.collectLatest {state ->
            updateState { state }
        }
    }

    private fun shouldShowInHouseConsentMessage(
        gdrpConsentStatus: ConsentStatus,
        inHouseConsentStatus: ConsentStatus
    ): Boolean {
        val gdrpNotNeeded = gdrpConsentStatus in listOf(ConsentNotNeeded, Unknown)
        return inHouseConsentStatus == ConsentNeeded && gdrpNotNeeded
    }

    fun loadConsentStatus(activity: Activity) {
        takeAction(Action.LoadConsentStatus(activity))
    }

    fun onInHouseConsentGiven() {
        takeAction(Action.UpdateInHouseConsentStatus(ConsentGiven))
    }

    fun forceGDRPToBeIgnored(activity: Activity) {
        takeAction(Action.ForceGDRPConsent(activity, Unknown))
    }

    sealed class Action {
        data class LoadConsentStatus(val activity: Activity): Action()
        data class UpdateInHouseConsentStatus(val status: ConsentStatus): Action()
        data class ForceGDRPConsent(val activity: Activity, val status: ConsentStatus): Action()
    }

    data class State(
        val shouldShowInHouseConsentMessage: Boolean,
        val shouldShowGDRPConsentMessage: Boolean,
        val isLoading: Boolean
    )
}