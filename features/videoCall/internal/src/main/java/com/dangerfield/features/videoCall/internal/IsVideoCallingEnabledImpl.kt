package com.dangerfield.features.videoCall.internal

import com.dangerfield.features.videoCall.IsVideoCallingEnabled
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class IsVideoCallingEnabledImpl @Inject constructor(
    private val recognizedVideoCallingPlatforms: RecognizedVideoCallingPlatforms
): IsVideoCallingEnabled {

    override operator fun invoke(): Boolean {
        return recognizedVideoCallingPlatforms().isNotEmpty()
    }
}