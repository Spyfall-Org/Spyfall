package com.dangerfield.libraries.dictionary

/**
 * Used to determine if a message should be shown to the user about the level of support
 * we have for their detected language
 */
interface ShouldShowLanguageSupportMessage {
    suspend operator fun invoke(
        languageSupportLevel: LanguageSupportLevel,
    ): Boolean
}


/**
 * Used to mark the langauge support message as shown
 * @param languageSupportLevel the level of support we have for the user's detected language
 */
interface LanguageSupportMessageShown {
    suspend operator fun invoke(
        languageSupportLevel: LanguageSupportLevel,
    )
}