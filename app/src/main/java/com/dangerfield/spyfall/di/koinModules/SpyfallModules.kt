package com.dangerfield.spyfall.di.koinModules

val legacySpyfallModules = listOf(
    mainModule
)

val spyfallModules = listOf(
    migrationModule,
    settingsModule,
    welcomeModule,
    appModule
)

