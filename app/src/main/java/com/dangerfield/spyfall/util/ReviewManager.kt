package com.dangerfield.spyfall.util

interface ReviewManager {
    fun shouldPromptForReview() :Boolean
    fun setHasClickedToReview()
}