#!/usr/bin/env kotlin

@file:Import(
    "data/AndroidClientInfo.kt",
    "data/ApiKey.kt",
    "data/AppinviteService.kt",
    "data/Client.kt",
    "data/ClientInfo.kt",
    "data/GoogleServices.kt",
    "data/IosInfo.kt",
    "data/OauthClient.kt",
    "data/OtherPlatformOauthClient.kt",
    "data/ProjectInfo.kt",
    "data/Services.kt",
)
@file:Import("util/GithubActionsUtil.main.kts")
@file:DependsOn("com.google.code.gson:gson:2.8.6")

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.OutputStreamWriter
import java.util.Properties
import com.google.gson.Gson

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

val googleServicesGsonPath = "app/src/debug/google-services.json"
val releaseGoogleServicesGsonPath = "app/src/release/google-services.json"


@Suppress("ComplexCondition", "MagicNumber")
if (args.size < 2 || args[0] == "-h" || args[0] == "--help" || args[0].contains("help")) {
    printRed(
        """
        This script sets env variables for a pr workflow run
        
        usage: ./set_pr_env_variables.main.kts [branch_name] [env_file]
        [branch_name] - branch that triggered the workflow using this script
        [env_file] - env file used to store output of this script 
        [pull_number] - the number of the pull request that triggered this 
        
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("UseCheckOrError", "ThrowsCount")
fun main() {
    val branchName = args[0]
    val envFile = File(args[1])
    val pullNumber = args[2]

    val writer = envFile.writer()

    setReleaseVariables(writer, branchName)
    setAppFirebaseLinks(writer)
    setReleaseNotes(writer, pullNumber)
    setPullRequestLink(writer, pullNumber)
    setAppId(writer)

    writer.close()
}

fun setPullRequestLink(writer: OutputStreamWriter, pullNumber: String) {
    writer.writeEnvValue("pullRequestLink", "https://github.com/oddoneoutgame/OddOneOut/pull/$pullNumber")
}

@Suppress("MaxLineLength")
fun setReleaseNotes(writer: OutputStreamWriter, pullNumber: String) {
    val releaseNotes = """
        :warning: :warning: :warning: 
        ```diff
        - Please update this release draft with notes about the included changes before publishing.
        ```
        :warning: :warning: :warning:
        
        ## [PR that triggered draft](https://github.com/oddoneoutgame/OddOneOut/pull/$pullNumber)
        When you publish, please merge the above Pull Request back into main.
    """.trimIndent()

    val releaseNotesFile = File("release_notes_temp.md").also { it.createNewFile() }

    releaseNotesFile.writer().apply {
        write(releaseNotes)
        close()
    }

    writer.writeEnvValue("releaseNotesFile", releaseNotesFile.path)
}

fun setAppId(writer: OutputStreamWriter) {
    writer.writeEnvValue("oddoneoutDebugAppId", getDebugAppId())
    writer.writeEnvValue("oddoneoutReleaseAppId", getReleaseAppId())
}

fun setAppFirebaseLinks(writer: OutputStreamWriter) {
    val debugProjectId = getDebugFirebaseProjectId()
    val releaseProjectId = getReleaseFirebaseProjectId()
    val packageName = getPackageName()

    @Suppress ("MaxLineLength")
    val debugLink = "https://console.firebase.google.com/u/0/project/${debugProjectId}/appdistribution/app/android:${packageName + ".debug"}/releases"
    val releaseLink = "https://console.firebase.google.com/u/0/project/${releaseProjectId}/appdistribution/app/android:${packageName}/releases"

    writer.writeEnvValue("oddoneoutDebugFirebaseDistributionLink", debugLink)
    writer.writeEnvValue("oddoneoutReleaseFirebaseDistributionLink", releaseLink)
}

fun getDebugAppId(): String {
    val googleServicesObject = Gson().fromJson(FileReader(googleServicesGsonPath), GoogleServices::class.java)
    val appPackageName = getPackageName() + ".debug"
    val appId = googleServicesObject
        .client
        .firstOrNull { it.client_info.android_client_info.package_name.equals(appPackageName) }
        ?.client_info
        ?.mobilesdk_app_id

    check(appId != null) { "Could not find the app id for package name $appPackageName from the google services file for the project" }

    return appId
}

fun getReleaseAppId(): String {
    val googleServicesObject = Gson().fromJson(FileReader(releaseGoogleServicesGsonPath), GoogleServices::class.java)
    val appPackageName = getPackageName()
    val appId = googleServicesObject
        .client
        .firstOrNull { it.client_info.android_client_info.package_name.equals(appPackageName) }
        ?.client_info
        ?.mobilesdk_app_id

    check(appId != null) { "Could not find the app id for package name $appPackageName from the google services file for the project" }

    return appId
}

fun getDebugFirebaseProjectId(): String {
    val googleServicesObject = Gson().fromJson(FileReader(googleServicesGsonPath), GoogleServices::class.java)
    return googleServicesObject.project_info.project_id
}

fun getReleaseFirebaseProjectId(): String {
    val googleServicesObject = Gson().fromJson(FileReader(releaseGoogleServicesGsonPath), GoogleServices::class.java)
    return googleServicesObject.project_info.project_id
}

fun getPackageName(): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("packageName")
}


@Suppress("UseCheckOrError", "ThrowsCount")
fun setReleaseVariables(writer: OutputStreamWriter, branchName: String) {
    // matches "release-words-dashes_underscores-and-num6ers/vx.y.z
    val releaseBranchPattern = Regex("release-[\\w-]+/v\\d+\\.\\d+\\.\\d+")

    val isRelease = releaseBranchPattern.matches(branchName)

    writer.writeEnvValue("isReleasePR", "$isRelease")

    if (!isRelease) return

    val vXyzMatcher = "v\\d+\\.\\d+\\.\\d+".toRegex()
    val vXyMatcher = "v\\d+\\.\\d+".toRegex()

    val branchVersion = (vXyzMatcher.find(branchName) ?: vXyMatcher.find(branchName))?.value ?: throw
    IllegalStateException(" branch detected to be a release but could not extract the version.")

    val appVersionName = getVersionName()

    if (!branchVersion.contains(appVersionName)) {
        throw IllegalStateException(
            """
            Branch name lists version as $branchVersion which does not contain the apps version $appVersionName.
        """.trimIndent()
        )
    }

    writer.writeEnvValue("releaseVersion", "$branchVersion")
    writer.writeEnvValue("releaseTagName", "oddoneout/$branchVersion")
}

fun getVersionName(): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()

    return properties.getProperty("versionName").toString()
}

main()
