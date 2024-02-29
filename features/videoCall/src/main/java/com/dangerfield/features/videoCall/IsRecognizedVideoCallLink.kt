package com.dangerfield.features.videoCall

interface IsRecognizedVideoCallLink {
    operator fun invoke(link: String): Boolean
}