package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import android.os.Build
import com.dangerfield.libraries.dictionary.GetDeviceLanguageSupportLevel
import com.dangerfield.libraries.dictionary.LanguageSupportLevel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import oddoneout.core.BuildInfo
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@AutoBind
class GetDeviceLanguageSupportLevelImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val buildInfo: BuildInfo,
    @ApplicationContext private val applicationContext: Context
) : GetDeviceLanguageSupportLevel {

    private val appLocale: Locale
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            applicationContext.resources.configuration.locales[0]
        } else {
            applicationContext.resources.configuration.locale
        }

    private val deviceLocale = Locale.getDefault()

    override suspend fun invoke(): LanguageSupportLevel = if (!isDeviceLanguageSupported()) {
        LanguageSupportLevel.NotSupported(deviceLocale)
    } else {
        getLanguageSupportLevel(appLocale)
    }.also {
        Timber.d("Language support level for ${it.locale.displayName} is $${it::class.simpleName}")
    }

    /**
     * if the app doesnt even has the resources for the language, then we dont support it at all
     * otherwise we at least have something to show them, just might not be perfect
     */
    private fun isDeviceLanguageSupported() = deviceLocale.language == appLocale.language

    private suspend fun getLanguageSupportLevel(locale: Locale): LanguageSupportLevel =
        firebaseFirestore.collection(SUPPORTED_LANGUAGES_COLLECTION_KEY)
            .document(buildInfo.versionName)
            .get()
            .await()
            .getField<String>(locale.language)
            .let {
                when (it) {
                    SUPPORT_LEVEL_FULL -> LanguageSupportLevel.Supported(locale)
                    SUPPORT_LEVEL_PARTIAL -> LanguageSupportLevel.PartiallySupported(locale)
                    else -> LanguageSupportLevel.NotSupported(locale)
                }
            }

    companion object {
        private const val SUPPORTED_LANGUAGES_COLLECTION_KEY = "supported-languages-android"
        private const val SUPPORT_LEVEL_FULL = "full"
        private const val SUPPORT_LEVEL_PARTIAL = "partial"

    }
}