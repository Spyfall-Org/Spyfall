package com.dangerfield.features.forcedupdate.internal

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import spyfallx.core.BuildInfo
import java.util.concurrent.CancellationException
import javax.inject.Inject

class SpyfallAppUpdateDataSource @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val buildInfo: BuildInfo
) : AppUpdateDataSource {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getMinimumVersionCode(): Int? {
        val result = try {
            firebaseFirestore.collection(buildInfo.configKey)
                .document(buildInfo.versionName)
                .get()
                .await()
                .get(requiredVersionCodeField) as? Long
        } catch (e: Exception) {
            if (e is CancellationException) throw e else null
        }

        return result?.toInt()
    }

    companion object {
        private const val requiredVersionCodeField = "required_version_code"
    }
}
