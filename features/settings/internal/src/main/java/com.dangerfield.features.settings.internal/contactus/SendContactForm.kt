package com.dangerfield.features.settings.internal.contactus

import com.dangerfield.libraries.session.Session
import com.google.firebase.firestore.FirebaseFirestore
import oddoneout.core.BuildInfo
import oddoneout.core.Try
import oddoneout.core.awaitResult
import oddoneout.core.ignoreValue
import oddoneout.core.withBackoffRetry
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class SendContactForm @Inject constructor(
    private val clock: Clock,
    private val session: Session,
    private val buildInfo: BuildInfo,
    private val contactFormCollection: ContactFormCollection,
    private val firestore: FirebaseFirestore
) {
    suspend fun invoke(
        name: String,
        email: String,
        message: String,
        contactReason: ContactReason
    ): Try<Unit> {

        val timeZoneId = ZoneId.of("America/New_York")
        val localDateTime = Try { LocalDateTime.ofInstant(clock.instant(), timeZoneId) }
            .getOrNull()
            ?: LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault())

        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")

        return withBackoffRetry(retries = 3) {

            val data = hashMapOf(
                "name" to name,
                "email" to email,
                "message" to message,
                "reason" to contactReason.name,
                "uid" to session.user.id,
                "sessionId" to session.sessionId,
                "languageCode" to session.user.languageCode,
                "languageCode" to session.user.languageCode,
                "isResolved" to false,
                "appVersion" to buildInfo.versionName,
                "device" to buildInfo.deviceName,
                "calendarDate" to localDateTime.format(dateFormatter),
            )

            firestore.collection(contactFormCollection())
                .document(UUID.randomUUID().toString())
                .set(data)
                .awaitResult()
                .ignoreValue()
        }
    }
}