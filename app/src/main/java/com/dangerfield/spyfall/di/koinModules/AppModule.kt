package com.dangerfield.spyfall.di.koinModules

import spyfallx.coregameapi.GameRepository
import android.content.Context
import android.content.SharedPreferences
import com.dangerfield.spyfall.R
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import spyfallx.coregame.SpyfallRepository

/**
 * Hosts dependencies shared across multiple modules
 */
val appModule = module {

    factory<SharedPreferences> {
        androidApplication().getSharedPreferences(
            androidApplication().resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
    }

    factory<GameRepository> { SpyfallRepository()  }
}