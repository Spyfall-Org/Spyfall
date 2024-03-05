package com.dangerfield.features.settings.internal.referral

import androidx.datastore.core.DataStore
import com.dangerfield.libraries.session.Session
import kotlinx.coroutines.flow.firstOrNull
import oddoneout.core.Try
import java.util.Optional
import java.util.UUID
import javax.inject.Inject

class GetMeReferralCode @Inject constructor(
    private val session: Session,
    private val datastore: DataStore<Optional<ReferralCode>>,
    private val referralCodeLength: ReferralCodeLength,
){
    suspend operator fun invoke(): ReferralCode = Try {
        val cachedValue = datastore.data.firstOrNull()

        if (cachedValue != null && cachedValue.isPresent) {
            cachedValue.get()
        } else {
            newReferralCode()
        }

    }.getOrElse {
        newReferralCode()
    }

    private suspend fun newReferralCode(): ReferralCode {
        val length = referralCodeLength()
        val referralCode = ReferralCode(
            code = session.user.id?.take(length) ?: UUID.randomUUID().toString().take(length),
            redemptionStatus = RedemptionStatus.NotRedeemed
        )

        datastore.updateData { Optional.of(referralCode) }
        return referralCode
    }
}