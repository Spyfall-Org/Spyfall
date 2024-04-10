package com.dangerfield.libraries.dictionary

/**
 * Returns the langauge code being used by the app,
 *
 * this is not necessarily equal to the device langauge code as we may not support that.
 * Instead this value represents which strings.xml file we are using.
 */
interface GetAppLanguageCode {
    operator fun invoke(): String
}