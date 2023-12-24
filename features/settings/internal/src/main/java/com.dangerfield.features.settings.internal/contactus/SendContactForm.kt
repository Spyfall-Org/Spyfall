package com.dangerfield.features.settings.internal.contactus

import com.dangerfield.libraries.session.Session
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import spyfallx.core.Try
import spyfallx.core.withBackoffRetry
import java.time.Clock
import java.util.UUID
import javax.inject.Inject

class SendContactForm @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val clock: Clock,
    private val session: Session
) {

    suspend fun invoke(
        name: String,
        email: String,
        message: String,
        contactReason: ContactReason
    ): Try<Unit> {
        return withBackoffRetry(retries = 3) {
            Try {
                firebaseFirestore
                    .collection("contactForms")
                    .document(UUID.randomUUID().toString())
                    .set(
                        hashMapOf(
                            "name" to name,
                            "email" to email,
                            "message" to message,
                            "reason" to contactReason.name,
                            "timestamp" to clock.millis(),
                            "uid" to session.user.id,
                            "sessionId" to session.sessionId,
                            "languageCode" to session.user.languageCode,
                        )
                    )
                    .await()
            }.ignoreValue()
        }
    }
}