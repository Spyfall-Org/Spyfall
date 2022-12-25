#!/usr/bin/env kotlin

@file:DependsOn("org.kohsuke:github-api:1.125")

import org.kohsuke.github.GHAsset
import org.kohsuke.github.GHRelease
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.File

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"
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
        This script uploads the debug and 
        release assets for spyfall to the draft release (creating one if there is not yet one)
        
        Usage: ./create_andor_update_spyfall_release_draft [RELEASE_APK] [RELEASE_AAB] [DEBUG_APK] [GITHUBREPO] [GITHUBTOKEN] [RELEASENAME]
        arg1: The path to the signed spyfall release apk
        arg2: The path to the signed spyfall release aab
        arg3: The path to the spyfall debug apk
        arg4: The GITHUBREPO variable from the github workflow that called this script
        arg5: The GITHUBTOKEN variable from the github workflow that called this script
        arg6: The release name 

    """.trimIndent())

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("MagicNumber")
fun doWork() {
    val releaseApkPath = args[0]
    val releaseAabPath = args[1]
    val debugApkPath = args[2]
    val githubRepoInfo = args[3] // in the format: "REPO_OWNER/REPO_NAME"
    val githubToken = args[4]
    val releaseName = args[5]

    val releaseApk = File(releaseApkPath)
    val releaseAab = File(releaseAabPath)
    val debugApk = File(debugApkPath)

    val repo = getRepository(githubRepoInfo, githubToken)

    val release = getReleaseDraft(repo, releaseName) ?: createReleaseDraft(repo, releaseName)

    release.uploadApk(releaseApk)
    release.uploadApk(debugApk)
    release.uploadBundle(releaseAab)
}



fun getRepository(githubRepoInfo: String, githubToken: String): GHRepository =
    GitHub.connectUsingOAuth(githubToken).getRepository(githubRepoInfo)

fun getReleaseDraft(repo: GHRepository, releaseName: String): GHRelease? =
    repo.listReleases().firstOrNull { it.name == releaseName && it.isDraft }

fun createReleaseDraft(repo: GHRepository, releaseName: String): GHRelease =
     repo.createRelease(releaseName).draft(true).create()

fun GHRelease.uploadApk(apkFile: File): GHAsset =
    this.uploadAsset(apkFile, "application/vnd.android.package-archive")

fun GHRelease.uploadBundle(aabFile: File): GHAsset =
    this.uploadAsset(aabFile, "application/vnd.android.package-bundle")

doWork()
