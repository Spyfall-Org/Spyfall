package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.game.RealGameViewModel
import com.dangerfield.spyfall.joinGame.JoinGameViewModel
import com.dangerfield.spyfall.newGame.NewGameFragment
import com.dangerfield.spyfall.newGame.NewGameViewModel
import com.dangerfield.spyfall.waiting.WaitingViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single { Repository(FirebaseFirestore.getInstance()) as GameRepository }

    viewModel { JoinGameViewModel(get()) }
    viewModel { WaitingViewModel(get()) }
    viewModel { NewGameViewModel(get()) }
    viewModel { RealGameViewModel(get()) }


}