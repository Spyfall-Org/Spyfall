package com.dangerfield.libraries.dictionary

interface ShouldShowLanguageSupportMessage {
    suspend operator fun invoke(
        languageSupportLevel: LanguageSupportLevel,
    ): Boolean
}

interface LanguageSupportMessageShown {
    suspend operator fun invoke(
        languageSupportLevel: LanguageSupportLevel,
        )
}