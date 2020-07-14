package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.api.Constants
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.game.GameViewModel
import com.dangerfield.spyfall.joinGame.JoinGameViewModel
import com.dangerfield.spyfall.newGame.NewGameViewModel
import com.dangerfield.spyfall.start.StartViewModel
import com.dangerfield.spyfall.util.RemoveUserTimer
import com.dangerfield.spyfall.util.ReviewHelper
import com.dangerfield.spyfall.util.ReviewManager
import com.dangerfield.spyfall.waiting.WaitingViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single { Repository(FirebaseFirestore.getInstance(), get()) as GameRepository }
    single { RemoveUserTimer(get())}

    viewModel { JoinGameViewModel(get()) }
    viewModel { WaitingViewModel(get()) }
    viewModel { NewGameViewModel(get()) }
    viewModel { GameViewModel(get()) }
    viewModel { StartViewModel(get()) }


    factory { Constants(androidApplication()) }
    factory { ReviewHelper(androidContext()) as ReviewManager }
}