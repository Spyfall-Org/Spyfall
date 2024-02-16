package com.dangerfield.features.consent

interface OpenConsentForm {
    suspend operator fun invoke()
}

interface ShouldShowPrivacyOption {
    operator fun invoke(): Boolean
}