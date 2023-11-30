package com.dangerfield.features.newgame.internal.usecase

import javax.inject.Inject

class IsRecognizedVideoCallingUrl @Inject constructor(
    val recognizedVideoCallingPlatforms: RecognizedVideoCallingPlatforms
) {

    operator fun invoke(link: String): Boolean {
        val recognizedList = recognizedVideoCallingPlatforms().flatMap { it.value }

        val urlRegex = recognizedList.joinToString(separator = "|") { "(https?://)?(www\\.)?\\w+\\.$it(/\\S*)?" }.toRegex()

        return urlRegex.matches(link)
    }
}