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

include(":libraries:coreUi:internal")
include("app")
include("features:splash")
include("libraries:configApi")
include("libraries:coreApi")
include("libraries:coreCommon")
include("libraries:coreConfig")
include("libraries:coreGameApi")
include("libraries:coreSpyfall")
include("libraries:coreSpyfallGame")
include("libraries:coreUi")
include("libraries:coreWerewolfGame")
include("libraries:coreUi:internal")