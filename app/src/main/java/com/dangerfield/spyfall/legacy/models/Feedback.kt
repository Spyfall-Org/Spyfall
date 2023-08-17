package com.dangerfield.spyfall.legacy.models

data class Feedback(
    val message: String,
    val appVersion: String,
    val osVersion: String,
    val device: String,
    val apiLevel: String,
    val modelAndProduct: String,
    val date: String,
    val email: String
)
