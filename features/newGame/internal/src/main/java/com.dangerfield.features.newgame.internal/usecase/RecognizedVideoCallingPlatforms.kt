package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.libraries.config.AppConfigMap
import com.dangerfield.libraries.config.ConfiguredValue
import javax.inject.Inject

class RecognizedVideoCallingPlatforms @Inject constructor(
    private val config: AppConfigMap
): ConfiguredValue<Map<String, List<String>>>() {
    override val displayName: String
        get() = "Recognized Video Calling Platforms"

    override val default: Map<String, List<String>>
        get() = mapOf(
            "Zoom" to listOf("zoom.us"),
            "Google meets" to listOf("meet.google.com"),
            "Teams" to listOf("teams.microsoft.com"),
            "Webex" to listOf("webex.com"),
            "Skype" to listOf("skype.com"),
            "Go to meeting" to listOf("gotomeeting.com", "join.me"),
            "Blue jeans" to listOf("bluejeans.com"),
            "Slack" to listOf("slack.com"),
            "Whatsapp" to listOf("whatsapp.com"),
            "Messenger" to listOf("messenger.com"),
            "Viber" to listOf("viber.com"),
            "Facetime" to listOf("facetime.apple.com"),
            "Discord" to listOf("discord.com"),
            "Hangouts" to listOf("hangouts.google.com"),
            "Duo" to listOf("duo.google.com"),
            "Jitsi" to listOf("jitsi.org"),
            "Chime" to listOf("chime.aws"),
        )

    override fun resolveValue(): Map<String, List<String>> = config.value(this)
}