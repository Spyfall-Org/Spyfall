package com.dangerfield.spyfall.di.modules

val legacySpyfallModules = listOf(
    mainModule
)

val spyfallModules = listOf(
    migrationModule,
    settingsModule,
    welcomeModule
)