package com.dangerfield.libraries.dictionary

/**
 * Returns the langauge code being used by the app, this is not automatically equal to the device
 * langauge code as we may not support that
 */
interface GetAppLanguageCode {
    operator fun invoke(): String
}