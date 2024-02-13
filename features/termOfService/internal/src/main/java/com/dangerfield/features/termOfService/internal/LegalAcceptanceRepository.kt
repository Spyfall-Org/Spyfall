package com.dangerfield.features.termOfService.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.features.termOfService.LegalAcceptanceState
import com.dangerfield.libraries.storage.datastore.cache
import com.dangerfield.libraries.storage.datastore.distinctKeyFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import oddoneout.core.Try
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LegalAcceptanceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val acceptanceStateFlow = dataStore
        .distinctKeyFlow(LegalStateKey)
        .map { cachedValue ->
            cachedValue?.let { string ->
                Try { LegalAcceptanceState.valueOf(string) }
                    .getOrNull()
                    ?: LegalAcceptanceState.NotAccepted
            } ?: LegalAcceptanceState.NotAccepted
        }

    fun getAcceptanceStateFlow(): Flow<LegalAcceptanceState> = acceptanceStateFlow

    suspend fun updateAcceptanceState(legalAcceptanceState: LegalAcceptanceState?) {
        dataStore.cache(LegalStateKey, legalAcceptanceState?.name ?: "")
    }

    companion object {
        private val LegalStateKey = stringPreferencesKey("legal_state")
    }
}