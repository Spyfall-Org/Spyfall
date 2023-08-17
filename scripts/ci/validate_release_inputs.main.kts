#!/usr/bin/env kotlin

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

if (args.size < 2 || args[0] == "-h" || args[0].contains("help")) {
    printRed("""
        This script validates the inputs to the create_release_branch.yml scrtipt 
        
        Usage: ./validate_release_inputs.main.kts  [versionName] [versionCode]
        versionName (required): "1.2.4", "1.5.19", ...
        versionCode (optional): "500", "15", ...
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}


val appName = args[0]
val appVersion = args[1]
val versionCode = args.getOrNull(2)

class ValidationError(validationErrorMessage: String) :Exception(validationErrorMessage)

fun isVersionCodeValid() = versionCode == null || versionCode.toIntOrNull() != null && versionCode.toInt() > 0

fun isAppVersionValid(): Boolean {
    // Regular expression to match a version code of the form "x.y.z"
    val xyzMatcher = "\\d+\\.\\d+\\.\\d+".toRegex()
    // Regular expression to match a version code of the form "x.y"
    val xyMatcher = "\\d+\\.\\d+".toRegex()

    return xyzMatcher.matches(appVersion) || xyMatcher.matches(appVersion)
}

val appVersionValidationError = ValidationError("""
    The app version "$appVersion" is invalid. 
    Please make sure you enter an app version in the format x.y.z (major, minor, patch [optional])
""".trimIndent())

val versionCodeValidationError = ValidationError("""
    The version code "$versionCode" is invalid. 
    Please make sure you enter a numeric app version code
""".trimIndent())


when {
    !isAppVersionValid() -> throw appVersionValidationError
    !isVersionCodeValid() -> throw versionCodeValidationError
    else -> printGreen("Good news. The inputs to create the release are valid. ")
}
