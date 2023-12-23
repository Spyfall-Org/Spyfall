package com.dangerfield.features.videoCall.internal

import com.dangerfield.features.videoCall.IsRecognizedVideoCallLink
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class IsRecognizedVideoCallingUrl @Inject constructor(
    val recognizedVideoCallingPlatforms: RecognizedVideoCallingPlatforms
): IsRecognizedVideoCallLink {

    override operator fun invoke(link: String): Boolean {
        val recognizedList = recognizedVideoCallingPlatforms().flatMap { it.value }

        val urlRegex = recognizedList.joinToString(separator = "|") { "(https?://)?(www\\.)?(\\w+\\.)?$it(/\\S*)?" }.toRegex()

        return urlRegex.matches(link)
    }
}