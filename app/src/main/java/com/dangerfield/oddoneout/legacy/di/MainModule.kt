package com.dangerfield.oddoneout.legacy.di

import com.dangerfield.oddoneout.BuildConfig.VERSION_CODE
import com.dangerfield.oddoneout.BuildConfig.VERSION_NAME
import com.dangerfield.oddoneout.legacy.api.Constants
import com.dangerfield.oddoneout.legacy.api.FireStoreService
import com.dangerfield.oddoneout.legacy.api.GameRepository
import com.dangerfield.oddoneout.legacy.api.GameService
import com.dangerfield.oddoneout.legacy.api.Repository
import com.dangerfield.oddoneout.legacy.models.Session
import com.dangerfield.oddoneout.legacy.ui.game.GameViewModel
import com.dangerfield.oddoneout.legacy.ui.joinGame.JoinGameViewModel
import com.dangerfield.oddoneout.legacy.ui.newGame.NewGameViewModel
import com.dangerfield.oddoneout.legacy.ui.start.StartViewModel
import com.dangerfield.oddoneout.legacy.ui.waiting.WaitingViewModel
import com.dangerfield.oddoneout.legacy.util.DBCleaner
import com.dangerfield.oddoneout.legacy.util.FeedbackHelper
import com.dangerfield.oddoneout.legacy.util.PreferencesHelper
import com.dangerfield.oddoneout.legacy.util.PreferencesService
import com.dangerfield.oddoneout.legacy.util.RemoveUserTimer
import com.dangerfield.oddoneout.legacy.util.ReviewHelper
import com.dangerfield.oddoneout.legacy.util.SavedSessionHelper
import com.dangerfield.oddoneout.legacy.util.SessionListenerHelper
import com.dangerfield.oddoneout.legacy.util.SessionListenerService
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import oddoneout.core.BuildInfo

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
    factory {
        BuildInfo(
            versionCode = VERSION_CODE,
            versionName = VERSION_NAME,
            packageName = androidContext().packageName
        )
    }

    factory { FireStoreService(get(), get()) as GameService }
    factory { Constants(androidApplication()) }
    factory { ReviewHelper(androidContext()) }
    factory { SavedSessionHelper(get(), get()) }
    factory { FeedbackHelper(get(), get()) }
    factory { DBCleaner(get(), get()) }
}
