package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import android.os.Build
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import oddoneout.core.BuildInfo
import oddoneout.core.Try
import oddoneout.core.tryAwait
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
     *
     * TODO the dictionaries
     */
    private val locale: Locale
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            applicationContext.resources.configuration.locales[0]
        } else {
            applicationContext.resources.configuration.locale
        }

    private val localeString: String
        get() = locale.language

    // TODO this document contains support levels for each languge. Consider cahcing the result here
    // or changing things so that it can be returned here
    override suspend fun getDictionary(): Try<OverrideDictionary> = Try {
        Timber.d("Fetching dictionary for ${buildInfo.versionName} with locale: $localeString")

        val localeDictionaryMap = firebaseFirestore.collection(DICTIONARY_COLLECTION_KEY)
            .document(buildInfo.versionName)
            .get()
            .tryAwait()
            .let {
                it.getOrNull()?.data
            }
            ?.get(localeString)
            ?.let {
                Try {
                    it as Map<String, String>
                }.getOrNull()
            }
            .orEmpty()

        OverrideDictionary(
            map = localeDictionaryMap,
            context = applicationContext
        )
    }

    companion object {
        internal const val DICTIONARY_COLLECTION_KEY = "dictionary-overrides-android"
    }
}