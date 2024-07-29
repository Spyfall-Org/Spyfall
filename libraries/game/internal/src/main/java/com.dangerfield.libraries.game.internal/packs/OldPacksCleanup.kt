package com.dangerfield.libraries.game.internal.packs

import android.content.Context
import androidx.datastore.dataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import javax.inject.Inject
import javax.inject.Singleton

class CleanUpOldPacks @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke() {
       Catching {
           val file = context.dataStoreFile("location_packs")
           if (file.exists()) file.delete()
       }
           .logOnFailure()
    }
}