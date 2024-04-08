package com.dangerfield.libraries.dictionary.internal

import android.content.Context
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.oddoneoout.libraries.dictionary.internal.R
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class GetAppLanguageCodeImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
): GetAppLanguageCode {

    override fun invoke(): String {
        // let android decide. They will choose which string.xml to use based on the device language and out supported languages
        return applicationContext.getString(R.string.appLanguageCode)
    }
}