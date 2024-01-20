package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import android.os.Build
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import oddoneout.core.BuildInfo
import oddoneout.core.Try
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

/**
 * Fetches the dictionary map for the current app version from Firebase or empty if not found.
 */
@AutoBind
class FirebaseOverrideDictionaryDataSource
@Inject constructor(
    private val buildInfo: BuildInfo,
    private val firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val applicationContext: Context
) : OverrideDictionaryDataSource {

    /**
     * Gets the locale running in the app. Not necessarily the devices locale
     * The locale used depends on which locales we have support for in resources
     */
    private val locale: Locale
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            applicationContext.resources.configuration.locales[0]
        } else {
            applicationContext.resources.configuration.locale
        }

    private val localeString: String
        get() = locale.language

    // TODO consider exposing a flow and using a document observer to observe changes
    override suspend fun getDictionary(): Try<OverrideDictionary> = Try {
        val localeCollection = "$DICTIONARY_COLLECTION_KEY-$localeString"

        Timber.d("Fetching dictionary for ${buildInfo.versionName} with locale: $localeString")

        val dictionaryMap = firebaseFirestore.collection(localeCollection)
            .document(buildInfo.versionName)
            .get()
            .await()
            .data
            ?.mapNotNull { entry ->
                val value = Try { entry.value as? String }.getOrNull()
                value?.let { entry.key to it }
            }
            ?.toMap()
            .orEmpty()

        OverrideDictionary(
            map = dictionaryMap,
            context = applicationContext
        )
    }

    companion object {
        private const val DICTIONARY_COLLECTION_KEY = "dictionary-overrides-android"
    }
}