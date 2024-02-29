package com.dangerfield.features.consent.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.libraries.storage.datastore.cache
import com.dangerfield.libraries.storage.datastore.getValueFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * We show an in house consent screen for users that dont see the Admob consent screen
 * Essentially non EU users because as of the time of writing this Admob consent screens
 * are only supporting GDRP
 *
 * For others we need to get their consent in house.
 */
@Singleton
class InHouseConsentManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private val consentStatusFlow: Flow<ConsentStatus> = dataStore
        .getValueFlow(
            key = InHouseConsentStateKey,
            fromString = { InHouseConsentState.valueOf(it) }
        )
        .map {
            when (it) {
                InHouseConsentState.Accepted -> ConsentStatus.ConsentGiven
                InHouseConsentState.NotAccepted,
                null -> ConsentStatus.ConsentNeeded
            }
        }

    suspend fun getConsentStatus(): ConsentStatus = consentStatusFlow.first()

    fun getConsentStatusFlow(): Flow<ConsentStatus> = consentStatusFlow

    suspend fun updateConsentStatus(consentStatus: ConsentStatus) {
        val inHouseConsentStatus = when (consentStatus) {
            ConsentStatus.ConsentGiven -> InHouseConsentState.Accepted
            ConsentStatus.ConsentDenied,
            ConsentStatus.ConsentNeeded,
            ConsentStatus.Unknown,
            ConsentStatus.ConsentNotNeeded -> InHouseConsentState.NotAccepted
        }

        dataStore.cache(InHouseConsentStateKey, inHouseConsentStatus.name)
    }

    companion object {
        private val InHouseConsentStateKey = stringPreferencesKey("legal_state")
    }

    private enum class InHouseConsentState {
        Accepted, NotAccepted
    }
}

