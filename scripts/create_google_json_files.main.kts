import java.io.File

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

if (args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))) {
    printRed("""
        This script adds an empty file for every required google-services.json
        
        Usage: ./create_google_json_files.main.kts 
    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

fun createGoogleJsonFile(path: String) {
    val googleJsonFile = File("$path/google-services.json")

    if (googleJsonFile.createNewFile()) {
        printGreen("Test Google Services Json File created successfully")
    } else {
        printRed("Could not create Test Google Services Json File")
        @Suppress("TooGenericExceptionThrown")
        throw Exception("See Message Above")
    }
}

val pathToAppsDirectory = "apps"
val appsDirectory = File(pathToAppsDirectory)
val appDirectoryNames = appsDirectory.listFiles { file -> file.isDirectory }?.map { it.name } ?: listOf()

// adds google json file for every app directory
appDirectoryNames.forEach {
    createGoogleJsonFile("apps/$it")
}
