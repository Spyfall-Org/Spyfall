package com.dangerfield.libraries.dictionary.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dangerfield.libraries.dictionary.LanguageSupportLevel
import com.dangerfield.libraries.dictionary.LanguageSupportMessageShown
import com.dangerfield.libraries.dictionary.ShouldShowLanguageSupportMessage
import com.dangerfield.libraries.storage.datastore.cache
import com.dangerfield.libraries.storage.datastore.getValue
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import oddoneout.core.BuildInfo
import oddoneout.core.readJson
import oddoneout.core.toJson
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject

private val LanguageSupportMessagesKey = stringPreferencesKey("language_support_messages")

@JsonClass(generateAdapter = true)
data class MessageShown(
    val versionCode: Int,
    val languageSupportLevel: String,
    val language: String
)

/**
 * Determines if the language support message should be shown
 * @param languageSupportLevel the language support level
 * @return true if the message should be shown, false otherwise
 *
 * we show a message if that message has not been shown for the current language, support level and version code
 */
@AutoBind
class ShouldShowLanguageSupportMessageImpl @Inject constructor(
    private val buildInfo: BuildInfo,
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi
): ShouldShowLanguageSupportMessage {

    override suspend fun invoke(
        languageSupportLevel: LanguageSupportLevel,
    ): Boolean {

        val canShow = when (languageSupportLevel) {
            is LanguageSupportLevel.NotSupported,
            is LanguageSupportLevel.PartiallySupported -> true
            is LanguageSupportLevel.Supported,
            is LanguageSupportLevel.Unknown -> false
        }

        Timber.d("Can show language support message: $canShow")

        if (!canShow) return false

        Timber.d("""
            Checking if language support message has been shown
            | language: ${languageSupportLevel.locale.language} 
            | support level: ${languageSupportLevel.name}  
            | version code: ${buildInfo.versionCode}""".trimIndent())

        val language = languageSupportLevel.locale.language
        val messagesShown = dataStore.getValue(LanguageSupportMessagesKey) {
            moshi.readJson<List<MessageShown>>(it)
        }

        val hasShownAlready = messagesShown?.any {
            it.language == language
                    && it.languageSupportLevel == languageSupportLevel.name
                    && it.versionCode == buildInfo.versionCode
        } ?: false

        Timber.d("Has shown language support message already: $hasShownAlready")

        return !hasShownAlready
    }
}

@AutoBind
class LanguageSupportMessageShownImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
    private val buildInfo: BuildInfo
): LanguageSupportMessageShown {

    override suspend fun invoke(
        languageSupportLevel: LanguageSupportLevel,
    ) {
        val messagesShown = dataStore.getValue(LanguageSupportMessagesKey) {
            moshi.readJson<List<MessageShown>>(it)
        } ?: emptyList()

        val newMessagesShown = messagesShown + MessageShown(
            versionCode = buildInfo.versionCode,
            languageSupportLevel = languageSupportLevel.name,
            language = languageSupportLevel.locale.language
        )

        dataStore.cache(
            LanguageSupportMessagesKey,
            moshi.toJson(newMessagesShown)
        )
    }
}
