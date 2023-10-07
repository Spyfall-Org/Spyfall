pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

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
include("features:splash")
include("libraries:coreConfig")
include("libraries:configApi")
