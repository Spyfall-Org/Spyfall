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
    setAppIds(writer)
    setAppFirebaseLinks(writer)
    setReleaseNotes(writer, pullNumber)
    setPullRequestLink(writer, pullNumber)

    writer.close()
}

fun setPullRequestLink(writer: OutputStreamWriter, pullNumber: String) {
    writer.writeEnvValue("pullRequestLink", "https://github.com/Spyfall-Org/Spyfall/pull/$pullNumber")
}

@Suppress("MaxLineLength")
fun setReleaseNotes(writer: OutputStreamWriter, pullNumber: String) {
    val releaseNotes = """
        :warning: :warning: :warning: 
        ```diff
        - Please update this release draft with notes about the included changes before publishing.
        ```
        :warning: :warning: :warning:
        
        ## [PR that triggered draft](https://github.com/Spyfall-Org/Spyfall/pull/$pullNumber)
        When you publish, please merge the above Pull Request back into main.
         
        See the [release documentation](https://spyfall-org.github.io/how-to/release/) for more info. 
        
    """.trimIndent()

    val releaseNotesFile = File("release_notes_temp.md").also { it.createNewFile() }

    releaseNotesFile.writer().apply {
        write(releaseNotes)
        close()
    }

    writer.writeEnvValue("releaseNotesFile", releaseNotesFile.path)
}

fun setAppId(writer: OutputStreamWriter) {
    writer.writeEnvValue("spyfallAppId", getAppId())
}

fun setAppFirebaseLinks(writer: OutputStreamWriter) {
    val projectId = getFirebaseProjectId()
    val packageName = getPackageName()

    @Suppress ("MaxLineLength")
    val link = "https://console.firebase.google.com/u/0/project/${projectId}/appdistribution/app/android:${packageName}/releases"

    writer.writeEnvValue("spyfallFirebaseDistributionLink", link)
}

fun getAppId(): String {
    val googleServicesPath = "app/google-services.json"
    val googleServicesObject = Gson().fromJson(FileReader(googleServicesPath), GoogleServices::class.java)
    val appPackageName = getPackageName(appName)
    val appId = googleServicesObject
        .client
        .firstOrNull { it.client_info.android_client_info.package_name == appPackageName }
        ?.client_info
        ?.mobilesdk_app_id

    check(appId != null) { "Could not find the app id from the google services file for the project $appName" }

    return appId
}

fun getFirebaseProjectId(): String {
    val googleServicesPath = "app/google-services.json"
    val googleServicesObject = Gson().fromJson(FileReader(googleServicesPath), GoogleServices::class.java)
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

    writer.writeEnvValue("isRelease", "$isRelease")

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
    writer.writeEnvValue("releaseTagName", "spyfall/$branchVersion")
}

fun getVersionName(): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app.properties"))
    properties.load(reader)
    reader.close()

    return properties.getProperty("versionName").toString()
}

main()
