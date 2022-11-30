package com.dangerfield.spyfall.splash

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import spyfallx.core.BuildInfo
import java.util.concurrent.CancellationException
import javax.inject.Inject

private const val CONFIG_COLLECTION = "config"
private const val REQUIRED_VERSION_CODE_DOCUMENT = "required_version_code"
private const val REQUIRED_VERSION_CODE_FIELD = "code"

class CheckForRequiredSpyfallUpdate @Inject constructor(
    private val buildInfo: BuildInfo,
    private val firebaseFireStore: FirebaseFirestore
) : CheckForRequiredUpdate {
    override suspend fun shouldRequireUpdate(): Boolean {
        val minimumVersionCode = try {
            firebaseFireStore.collection(CONFIG_COLLECTION)
                .document(REQUIRED_VERSION_CODE_DOCUMENT)
                .get()
                .await()
                .get(REQUIRED_VERSION_CODE_FIELD) as Long
        } catch (e: Exception) {
            if (e is CancellationException) throw e else null
        }

        return minimumVersionCode != null && buildInfo.versionCode < minimumVersionCode
    }
}
