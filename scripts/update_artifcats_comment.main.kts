#!/usr/bin/env kotlin

@file:DependsOn("org.kohsuke:github-api:1.125")

import org.kohsuke.github.GHAsset
import org.kohsuke.github.GHRelease
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Properties

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

@Suppress("MagicNumber")
val minArgs = 6

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if ( isHelpCall || args.size < minArgs) {
    @Suppress("MaxLineLength")
    printRed("""
        This script comments a link to the PR of the artifacts generated for that PR
        
        Usage: ./update_artifcats_comment.main.kts [RELEASE_APK] [DEBUG_APK] [GITHUBREPO] [GITHUBTOKEN] [APP_NAME] [GITHUB_RUN_NUMBER] [GITHUB_PULL_REQUEST]
        arg1: The path to the signed release apk
        arg2: The path to the debug apk
        arg3: The GITHUBREPO variable from the github workflow that called this script
        arg4: The GITHUBTOKEN variable from the github workflow that called this script
        arg5: The app name [spyfall, werewolf...]
        arg6: The run number (used to differentiate builds)
        arg7: The pull number (used to comment on the pull request)

    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("MagicNumber")
fun doWork() {
    val releaseApkPath = args[0]
    val debugApkPath = args[1]
    val githubRepoInfo = args[2] // in the format: "REPO_OWNER/REPO_NAME"
    val githubToken = args[3]
    val appName = args[4].toLowerCase()
    val runNumber = args[5]
    val pullNumber = args[6]


    val releaseApk = File(releaseApkPath)
    val debugApk = File(debugApkPath)
    val appVersion = getAppVersion(appName)
    val releaseName = "$appName-v$appVersion"

    val repo = getRepository(githubRepoInfo, githubToken)

    repo.getWorkflow(0).

    val releaseAssetUrl = release.assetsUrl
    val baseMessage = "This release version has finished building. You can find the assets here: "
    val containsBotComment = repo
        .getPullRequest(pullNumber.toInt())
        .comments
        .any { it.body.contains(baseMessage) }

    if(!containsBotComment) {
        repo.getPullRequest(pullNumber.toInt()).comment("$baseMessage $releaseAssetUrl")
    }
}


fun getAppVersion(appName: String): String {
    val properties = Properties()
    val reader = BufferedReader(FileReader("app_versions.properties"))
    properties.load(reader)
    reader.close()
    return properties.getProperty("$appName.versionName").toString()
}

fun getRepository(githubRepoInfo: String, githubToken: String): GHRepository =
    GitHub.connectUsingOAuth(githubToken).getRepository(githubRepoInfo)

fun getReleaseDraft(repo: GHRepository, releaseName: String): GHRelease? =
    repo.listReleases().firstOrNull { it.name == releaseName && it.isDraft }

fun createReleaseDraft(repo: GHRepository, releaseName: String): GHRelease =
     repo
         .createRelease(releaseName)
         .draft(true)
         .create()

fun GHRelease.uploadApk(apkFile: File): GHAsset =
    this.uploadAsset(apkFile, "application/vnd.android.package-archive")

doWork()
