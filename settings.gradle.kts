pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
include("features:joinGame")
include("features:settings")
include("features:splash")
include("features:createGame:spyfall")
include("features:waiting")
include("features:welcome")
include("libraries:coreConfig")
include("libraries:configApi")
include("features:splash:spyfall")
