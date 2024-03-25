package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.internal.model.BasicMapBasedAppConfigMapMap
import com.dangerfield.libraries.config.AppConfigMap
import com.google.firebase.firestore.FirebaseFirestore
import se.ansman.dagger.auto.AutoBind
import oddoneout.core.BuildInfo
import oddoneout.core.Catching
import oddoneout.core.awaitCatching
import timber.log.Timber
import javax.inject.Inject

/**
 * Fetches the config map for the current app version from Firebase or empty if not found.
 */
@AutoBind
class FirebaseConfigDataSource
@Inject constructor(
    private val buildInfo: BuildInfo,
    private val firebaseFirestore: FirebaseFirestore,
) : ConfigDataSource {

    // TODO add targeted overrides for specific users, locales, etc.
    // TODO consider exposing a flow and using a document observer to observe changes
    // every active session everywhere would be observing that document, not sure if thats great.
    override suspend fun getConfig(): Catching<AppConfigMap> = Catching {
        Timber.d("Fetching config for ${buildInfo.versionName}")
        val configMap = firebaseFirestore
            .collection(CONFIG_COLLECTION_KEY)
            .document(buildInfo.versionName)
            .get()
            .awaitCatching()
            .getOrThrow()
            .data
            .also {
                if (it.isNullOrEmpty()) {
                    Timber.d("No document config found for ${buildInfo.versionName} in the collection $CONFIG_COLLECTION_KEY")
                } else {
                    Timber.d("firebase Document config found for ${buildInfo.versionName} was: \n $it")
                }
            }
            .orEmpty()

        BasicMapBasedAppConfigMapMap(configMap)
    }

    companion object {
        private const val CONFIG_COLLECTION_KEY = "config-android"
    }
}
