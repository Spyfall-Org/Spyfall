package com.dangerfield.spyfall.legacy.di

import com.dangerfield.spyfall.legacy.api.Constants
import com.dangerfield.spyfall.legacy.api.FireStoreService
import com.dangerfield.spyfall.legacy.api.GameRepository
import com.dangerfield.spyfall.legacy.api.GameService
import com.dangerfield.spyfall.legacy.api.Repository
import com.dangerfield.spyfall.legacy.models.Session
import com.dangerfield.spyfall.legacy.ui.game.GameViewModel
import com.dangerfield.spyfall.legacy.ui.joinGame.JoinGameViewModel
import com.dangerfield.spyfall.legacy.ui.newGame.NewGameViewModel
import com.dangerfield.spyfall.legacy.ui.start.StartViewModel
import com.dangerfield.spyfall.legacy.ui.waiting.WaitingViewModel
import com.dangerfield.spyfall.legacy.util.DBCleaner
import com.dangerfield.spyfall.legacy.util.FeedbackHelper
import com.dangerfield.spyfall.legacy.util.PreferencesHelper
import com.dangerfield.spyfall.legacy.util.PreferencesService
import com.dangerfield.spyfall.legacy.util.RemoveUserTimer
import com.dangerfield.spyfall.legacy.util.ReviewHelper
import com.dangerfield.spyfall.legacy.util.SavedSessionHelper
import com.dangerfield.spyfall.legacy.util.SessionListenerHelper
import com.dangerfield.spyfall.legacy.util.SessionListenerService
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single { Repository(get(), get(), get()) as GameRepository }
    single { RemoveUserTimer(get(), get()) }
    single { FirebaseFirestore.getInstance() }
    single { PreferencesHelper(androidApplication()) as PreferencesService }

    // view models
    single { (currentSession: Session) -> WaitingViewModel(get(), currentSession) }
    single { (currentSession: Session) -> GameViewModel(get(), currentSession) }
    viewModel { JoinGameViewModel(get()) }
    viewModel { NewGameViewModel(get()) }
    viewModel { StartViewModel(get(), get()) }

    factory { SessionListenerHelper(get(), get()) as SessionListenerService }
    factory { FireStoreService(get(), get()) as GameService }
    factory { Constants(androidApplication(), get()) }
    factory { ReviewHelper(androidContext()) }
    factory { SavedSessionHelper(get(), get()) }
    factory { FeedbackHelper(get(), get()) }
    factory { DBCleaner(get(), get()) }
}
