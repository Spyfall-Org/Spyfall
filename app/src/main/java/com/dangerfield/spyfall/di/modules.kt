package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.joinGame.JoinGameViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single { Repository(FirebaseFirestore.getInstance()) }

    viewModel { JoinGameViewModel(get()) }
}