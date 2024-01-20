package com.dangerfield.libraries.dictionary

import java.util.Locale

interface GetDeviceLanguageSupportLevel {
    suspend operator fun invoke(): LanguageSupportLevel
}

sealed class LanguageSupportLevel(val locale: Locale) {
    class Supported(locale: Locale): LanguageSupportLevel(locale)
    class NotSupported(locale: Locale): LanguageSupportLevel(locale)
    class PartiallySupported(locale: Locale): LanguageSupportLevel(locale)
}