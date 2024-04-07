package com.dangerfield.libraries.dictionary

import java.util.Locale

/**
 * Used to determine the level of support we have for the user's detected language
 */
interface GetDeviceLanguageSupportLevel {
    suspend operator fun invoke(): LanguageSupportLevel
}

sealed class LanguageSupportLevel(val locale: Locale, val name: String) {
    /**
     * We have full support for the user's detected language. All strings are translated and available
     */
    class Supported(locale: Locale): LanguageSupportLevel(locale, "full")

    /**
     * We have no support for the user's detected language. No strings are translated or available
     */
    class NotSupported(locale: Locale): LanguageSupportLevel(locale, "none")

    /**
     * We have partial support for the user's detected language. Some strings are translated and available
     */
    class PartiallySupported(locale: Locale): LanguageSupportLevel(locale, "partial")

    /**
     * We have no information on the level of support for the user's detected language.
     */
    class Unknown(locale: Locale): LanguageSupportLevel(locale, "unknown")
}

val supportLevelNameMap = mapOf(
    "full" to LanguageSupportLevel.Supported::class,
    "none" to LanguageSupportLevel.NotSupported::class,
    "partial" to LanguageSupportLevel.PartiallySupported::class,
    "unknown" to LanguageSupportLevel.Unknown::class
)