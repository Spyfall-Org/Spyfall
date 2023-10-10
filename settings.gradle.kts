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
include("features:colorPicker")
include("features:colorPicker:internal")
include("features:forcedUpdate")
include("features:forcedUpdate:internal")
include("features:splash")
include("features:welcome")
include("libraries:configApi")
include("libraries:coreApi")
include("libraries:coreCommon")
include("libraries:coreConfig")
include("libraries:coreDataStore")
include("libraries:coreFlowroutines")
include("libraries:coreGameApi")
include("libraries:coreSpyfall")
include("libraries:coreSpyfallGame")
include("libraries:coreUi")
include("libraries:coreUi:internal")
include("libraries:coreWerewolfGame")
include("features:welcome:internal")