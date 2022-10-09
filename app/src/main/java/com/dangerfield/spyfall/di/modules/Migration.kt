package com.dangerfield.spyfall.di.modules

import com.dangerfield.spyfall.api.*
import com.dangerfield.spyfall.util.*
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * As the application is migrated from a monolith structure some things will still exist inside of main that need to be provided. Those things
 * will be added here until they find their final home in a module.
 */
val migrationModule = module {
    factory { Constants(androidApplication(), get()) }
    factory { SessionListenerHelper(get(), get()) as SessionListenerService }
    single { Repository(get(), get(), get()) as GameRepository }
    single { PreferencesHelper(androidApplication()) as PreferencesService }
    single { RemoveUserTimer(get(), get()) }
    single { FirebaseFirestore.getInstance() }
    factory { FireStoreService(get(), get()) as GameService }
}