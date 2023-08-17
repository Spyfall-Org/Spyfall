pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "Spyfall"

include("app")
include("libraries:coreUi")
include("libraries:coreWerewolfGame")
include("libraries:coreSpyfallGame")
include("libraries:coreGameApi")
include("libraries:coreSpyfall")
include("libraries:coreApi")
include("libraries:coreCommon")
include("features:createGame")
include("features:createGame:spyfall")
include("features:createGameApi")
include("features:joinGame")
include("features:joinGameApi")
include("features:settingsApi")
include("features:settings")
include("features:splash")
include("features:createGame:spyfall")
include("features:waitingApi")
include("features:waiting")
include("features:welcome")
include("libraries:coreConfig")
include("libraries:configApi")
include("features:splash:werewolf")
include("features:splash:spyfall")
