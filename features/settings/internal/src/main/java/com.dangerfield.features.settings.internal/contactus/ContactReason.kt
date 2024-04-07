package com.dangerfield.features.settings.internal.contactus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ContactReason : Parcelable {
    Question, Issue, Feedback, Suggestion, Other
}