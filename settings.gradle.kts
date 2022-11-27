pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "spyfall"

include("apps:spyfall")
include("apps:werewolf")
include("libraries:coreUi")
include("libraries:coreSpyfallGame")
include("libraries:coreGameApi")
include("libraries:core")
include("features:createGame")
include("features:createGameApi")
include("features:joinGame")
include("features:joinGameApi")
include("features:settingsApi")
include("features:settings")
include("features:splash")
include("features:splash:werewolf")
include("features:splash:spyfall")
include("features:waitingApi")
include("features:waiting")
include("features:welcome")
