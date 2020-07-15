package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.ui.game.GameViewModel
import com.dangerfield.spyfall.ui.joinGame.JoinGameViewModel
import com.dangerfield.spyfall.models.CurrentSession
import com.dangerfield.spyfall.ui.newGame.NewGameViewModel
import com.dangerfield.spyfall.util.RemoveUserTimer
import com.dangerfield.spyfall.util.ReviewHelper
import com.dangerfield.spyfall.util.ReviewManager
import com.dangerfield.spyfall.util.SessionListenerHelper
import com.dangerfield.spyfall.ui.waiting.WaitingViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single { Repository(get(), get(), get()) as GameRepository }
    single { RemoveUserTimer(get())}
    single { FirebaseFirestore.getInstance()}
    single { (currentSession: CurrentSession) -> WaitingViewModel(get(), currentSession)}
    single { (currentSession: CurrentSession) -> GameViewModel(get(), currentSession)}

    viewModel { JoinGameViewModel(get()) }
    viewModel { NewGameViewModel(get()) }

    factory { SessionListenerHelper(get(), get()) }
    factory { Constants(androidApplication()) }
    factory { ReviewHelper(androidContext()) as ReviewManager }
}