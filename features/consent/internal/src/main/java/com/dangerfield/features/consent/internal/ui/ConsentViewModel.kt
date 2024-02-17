package com.dangerfield.features.consent.internal.ui

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.ConsentStatus.*
import com.dangerfield.features.consent.internal.GDRPConsentManager
import com.dangerfield.features.consent.internal.InHouseConsentManager
import com.dangerfield.features.consent.internal.ui.ConsentViewModel.Action
import com.dangerfield.features.consent.internal.ui.ConsentViewModel.Event
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
    private val inHouseConsentManager: InHouseConsentManager
): SEAViewModel<State, Event, Action>() {

    override val initialState = State(
        shouldShowInHouseConsentMessage = false,
        shouldShowGDRPConsentMessage = false,
        isLoading = true
    )

    override suspend fun handleAction(action: Action) {
        when(action) {
            is Action.LoadConsentStatus ->  observeConsentStatus(action)
            is Action.OpenGDRPConsentForm -> gdrpConsentManager.showConsentForm(action.activity)
            is Action.UpdateInHouseConsentStatus -> inHouseConsentManager.updateConsentStatus(action.status)
        }
    }

    private suspend fun observeConsentStatus(action: Action.LoadConsentStatus) = viewModelScope.launch {
        combine(
            gdrpConsentManager.getConsentStatusFlow(action.activity),
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
        }.collectLatest {
            setState(it)
        }
    }

    private fun shouldShowInHouseConsentMessage(
        gdrpConsentStatus: ConsentStatus,
        inHouseConsentStatus: ConsentStatus
    ): Boolean {
        val gdrpNotNeeded = gdrpConsentStatus in listOf(ConsentNotNeeded, Unknown,)
        return inHouseConsentStatus == ConsentNeeded && gdrpNotNeeded
    }

    fun loadConsentStatus(activity: Activity) {
        takeAction(Action.LoadConsentStatus(activity))
    }

    fun openGDRPConsentForm(activity: Activity) {
        takeAction(Action.OpenGDRPConsentForm(activity))
    }

    fun onInHouseConsentGiven() {
        takeAction(Action.UpdateInHouseConsentStatus(ConsentGiven))
    }

    sealed class Event {

    }

    sealed class Action {
        data class LoadConsentStatus(val activity: Activity): Action()
        data class OpenGDRPConsentForm(val activity: Activity): Action()
        data class UpdateInHouseConsentStatus(val status: ConsentStatus): Action()
    }

    data class State(
        val shouldShowInHouseConsentMessage: Boolean,
        val shouldShowGDRPConsentMessage: Boolean,
        val isLoading: Boolean
    )
}