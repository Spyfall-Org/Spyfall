package com.dangerfield.libraries.dictionary.internal

import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.dictionary.GetDeviceLanguageSupportLevel
import com.dangerfield.libraries.dictionary.LanguageSupportLevel
import com.dangerfield.libraries.dictionary.supportLevelNameMap
import oddoneout.core.Catching
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@AutoBind
class GetDeviceLanguageSupportLevelImpl @Inject constructor(
    private val languageSupportLevel: CurrentLanguageSupportLevelString,
    private val getAppLanguageCode: GetAppLanguageCode
) : GetDeviceLanguageSupportLevel {

    private val appLanguage: String
        get() = getAppLanguageCode()

    private val deviceLocale: Locale get() = Locale.getDefault()

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

    private fun getLanguageSupportLevel(locale: Locale): Catching<LanguageSupportLevel> = Catching {
        languageSupportLevel.value
            .let {
                Timber.i("Language support level from config: $it")
                val clazz = supportLevelNameMap[it]
                when (clazz) {
                    LanguageSupportLevel.Supported::class -> LanguageSupportLevel.Supported(locale)
                    LanguageSupportLevel.PartiallySupported::class -> LanguageSupportLevel.PartiallySupported(locale)
                    LanguageSupportLevel.NotSupported::class -> LanguageSupportLevel.NotSupported(locale)
                    else -> LanguageSupportLevel.Unknown(locale)
                }
            }
    }
}