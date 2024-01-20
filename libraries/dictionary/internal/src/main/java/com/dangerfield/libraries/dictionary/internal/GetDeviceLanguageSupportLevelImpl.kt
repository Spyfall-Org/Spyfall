package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import com.dangerfield.libraries.dictionary.GetDeviceLanguageSupportLevel
import com.dangerfield.libraries.dictionary.LanguageSupportLevel
import com.dangerfield.libraries.dictionary.internal.FirebaseOverrideDictionaryDataSource.Companion.DICTIONARY_COLLECTION_KEY
import com.dangerfield.libraries.dictionary.supportLevelNameMap
import com.dangerfield.oddoneoout.libraries.dictionary.R
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import oddoneout.core.BuildInfo
import oddoneout.core.Try
import oddoneout.core.getOrElse
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

    private val appLanguage: String
        get() = applicationContext.getString(R.string.appLanguageCode)

    private val deviceLocale = Locale.getDefault()

    override suspend fun invoke(): LanguageSupportLevel = if (!isDeviceLanguageSupported()) {
        LanguageSupportLevel.NotSupported(deviceLocale)
    } else {
        getLanguageSupportLevel(deviceLocale).getOrElse { LanguageSupportLevel.Unknown(deviceLocale) }
    }.also {
        Timber.d("""
            Device Language: ${deviceLocale.language}
            App Language: $appLanguage
            Language support level for ${it.locale.language} is ${'$'}${it::class.simpleName}
        """.trimIndent())
    }

    /**
     * if the app doesnt even has the resources for the language, then we dont support it at all
     * otherwise we at least have something to show them, just might not be perfect
     */
    private fun isDeviceLanguageSupported() = (deviceLocale.language == appLanguage).also {
        Timber.d("isDeviceLanguageSupported: $it")
    }

    private suspend fun getLanguageSupportLevel(locale: Locale): Try<LanguageSupportLevel> = Try {
        firebaseFirestore.collection(DICTIONARY_COLLECTION_KEY)
            .document(buildInfo.versionName)
            .get()
            .await()
            .data
            ?.get(SUPPORT_LEVEL_FIELD_KEY)
            ?.let {
                Try {
                    val supportLevelMap = (it as Map<String, String>)
                    supportLevelMap[locale.language]
                }.getOrNull()
            }
            .let {
                val clazz = supportLevelNameMap[it]
                when (clazz) {
                    LanguageSupportLevel.Supported::class -> LanguageSupportLevel.Supported(locale)
                    LanguageSupportLevel.PartiallySupported::class -> LanguageSupportLevel.PartiallySupported(locale)
                    LanguageSupportLevel.NotSupported::class -> LanguageSupportLevel.NotSupported(locale)
                    else -> LanguageSupportLevel.NotSupported(locale)
                }
            }
    }

    companion object {
        private const val SUPPORT_LEVEL_FIELD_KEY = "supportLevels"
    }
}