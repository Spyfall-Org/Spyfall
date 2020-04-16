package com.dangerfield.spyfall.di

import com.dangerfield.spyfall.api.Repository
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val mainModule = module {

    single { Repository(FirebaseFirestore.getInstance()) }

}