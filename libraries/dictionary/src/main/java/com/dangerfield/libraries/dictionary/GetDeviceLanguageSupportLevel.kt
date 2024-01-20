package com.dangerfield.libraries.dictionary

import java.util.Locale

interface GetDeviceLanguageSupportLevel {
    suspend operator fun invoke(): LanguageSupportLevel
}

sealed class LanguageSupportLevel(val locale: Locale, val name: String) {
    class Supported(locale: Locale): LanguageSupportLevel(locale, "full")
    class NotSupported(locale: Locale): LanguageSupportLevel(locale, "none")
    class PartiallySupported(locale: Locale): LanguageSupportLevel(locale, "partial")
    class Unknown(locale: Locale): LanguageSupportLevel(locale, "unknown")
}

val supportLevelNameMap = mapOf(
    "full" to LanguageSupportLevel.Supported::class,
    "none" to LanguageSupportLevel.NotSupported::class,
    "partial" to LanguageSupportLevel.PartiallySupported::class,
    "unknown" to LanguageSupportLevel.Unknown::class
)