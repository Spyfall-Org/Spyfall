package com.dangerfield.features.consent.internal

import android.app.Activity
import com.dangerfield.features.consent.ConsentStatus
import com.dangerfield.features.consent.ConsentStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class ConsentRepositoryImpl @Inject constructor(
    private val gdrpConsentManager: GDRPConsentManager,
    private val inHouseConsentManager: InHouseConsentManager
): ConsentStatusRepository {

    override fun getStatusFlow(activity: Activity): Flow<ConsentStatus> = combine(
        gdrpConsentManager.getConsentStatusFlow(activity),
        inHouseConsentManager.getConsentStatusFlow()
    ) { gdrpConsentStatus, inHouseConsentStatus ->

        Timber.d("gdrpConsentStatus: ${gdrpConsentStatus.name}, inHouseConsentStatus: ${inHouseConsentStatus.name}")

        when(gdrpConsentStatus) {
            ConsentStatus.ConsentGiven -> ConsentStatus.ConsentGiven
            ConsentStatus.ConsentDenied -> ConsentStatus.ConsentDenied
            ConsentStatus.ConsentNeeded ->  ConsentStatus.ConsentNeeded
            ConsentStatus.ConsentNotNeeded -> inHouseConsentStatus
            ConsentStatus.Unknown -> inHouseConsentStatus
        }
    }
}