#!/usr/bin/env kotlin

@file:Import("util/GithubActionsUtil.main.kts")
@file:DependsOn("org.kohsuke:github-api:1.319")
@file:DependsOn("com.squareup.okhttp3:okhttp:4.12.0")

import org.kohsuke.github.GHRelease
import org.kohsuke.github.GHRepository
import okhttp3.OkHttpClient
import org.kohsuke.github.extras.okhttp3.OkHttpConnector
import org.kohsuke.github.GitHubBuilder

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

@Suppress("MagicNumber")
val minArgs = 4

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

val isHelpCall = args.isNotEmpty() && (args[0] == "-h" || args[0].contains("help"))
if (isHelpCall || args.size < minArgs) {
    @Suppress("MaxLineLength")
    printRed(
        """
        This script comments a link to the PR of the artifacts generated for that PR
        
        Usage: ./update_pr_comment.main.kts [GITHUB_REPO] [GITHUB_TOKEN] [PULL_NUMBER] [RUN_ID] [RELEASE_LINK] [DEBUG_LINK] [RUN_NUMBER] [TAG_NAME]
        
        [GITHUB_REPO] - REPO_OWNER/REPO_NAME, provided by github actions as env variable
        [GITHUB_TOKEN] - token to interact with github provided by github actions as env variable or use PAT
        [PULL_NUMBER] - the number of the pull request
        [RUN_ID] - the number uniquely associated with this workflow run. Used to get artifacts url.
        [APP_TESTER_RELEASE_LINK] - Link to the firebase app test for the release build
        [APP_TESTER_DEBUG_LINK] - Link to the firebase app test for the debug build
        [APP_TESTER_DEBUG_FALLBACK_LINK] - Link to the firebase app tester debug general
        [APP_TESTER_RELEASE_FALLBACK_LINK] - Link to the firebase app tester release general
        [RUN NUMBER] - the github run number
        [TAG_NAME] - Optional, The name of the tag associated with the draft release created for this PR
        
    """.trimIndent()
    )

    @Suppress("TooGenericExceptionThrown")
    throw Exception("See Message Above")
}

@Suppress("MagicNumber")
fun doWork() {
    val githubRepoInfo = args[0] // in the format: "REPO_OWNER/REPO_NAME"
    val githubToken = args[1]
    val pullNumber = args[2]
    val runID = args[3]
    val appTesterReleaseLink = args[4]
    val appTesterDebugLink = args[5]
    val appTesterDebugFallBackLink = args[6]
    val appTesterReleaseFallBackLink = args[7]
    val runNumber = args[8]
    val tagName = args.getOrNull(10)

    val repo = getRepository(githubRepoInfo, githubToken)

    val releaseDraft = repo.listReleases().firstOrNull { it.isDraft && it.tagName == tagName }

    updatePRArtifactsComment(
        repo,
        runID.toLong(),
        pullNumber.toInt(),
        releaseDraft,
        appTesterReleaseLink,
        appTesterDebugLink,
        appTesterDebugFallBackLink,
        appTesterReleaseFallBackLink,
        runNumber,
        tagName
    )
}

@Suppress("LongParameterList")
fun updatePRArtifactsComment(
    repo: GHRepository,
    runID: Long,
    pullNumber: Int,
    releaseDraft: GHRelease?,
    appTesterReleaseLink: String,
    appTesterDebugLink: String,
    appTesterDebugFallbackLink: String,
    appTesterReleaseFallBackLink: String,
    buildNumber: String,
    tagName: String?
) {
    val htmlUrl = repo.getWorkflowRun(runID).htmlUrl

    val publishedReleaseUrl = "https://github.com/oddoneoutgame/OddOneOut/releases/tag/${tagName?.replace("/","%2F")}"

    @Suppress("MaxLineLength")
    val fullMessage = """
# Automated PR Assets Links  
${
        (if (appTesterDebugLink != "null") """
- ##### [App Tester Debug Build](${appTesterDebugLink}) 
""".trimIndent() else null) ?: ""
    }  
${
        (if (appTesterReleaseLink != "null") """
- ##### [App Tester Release Build](${appTesterReleaseLink}) 
""".trimIndent() else null) ?: ""
    }
${
        (if (appTesterReleaseLink == "null" && appTesterDebugLink == "null") """
- ##### [Debug App Tester](${appTesterDebugFallbackLink}) 
- ##### [Release App Tester](${appTesterReleaseFallBackLink}) 
""".trimIndent() else null) ?: ""
    }
${
        (if (releaseDraft != null) """
- ##### [Release Draft](${releaseDraft.htmlUrl}) 
- ##### [Release (once published)]($publishedReleaseUrl)
""".trimIndent() else null) ?: ""
    }
    
These assets are automatically generated on pull requests. Some links may not work until all jobs in the pull request workflow have finished. Every update to this PR will generate a new row in the assets table. 
        
| Commit | Build Number | Assets | 
|---|---|---|
""".trimIndent()

    @Suppress("MagicNumber")
    val lastCommitSha = repo.getPullRequest(pullNumber).head.sha.take(7)
    val baseMessage = "These assets are automatically generated on pull requests"

    val existingComment = repo
        .getPullRequest(pullNumber)
        .comments.firstOrNull { it.body.contains(baseMessage) }
        ?.body

    val assetsTableEntry = """
        |$lastCommitSha | $buildNumber | [Github Action Artifacts]($htmlUrl#artifacts) |
    """.trimIndent()

    val stringToComment = if (existingComment != null) {
        "$existingComment\n$assetsTableEntry"
    } else {
        "$fullMessage\n$assetsTableEntry"
    }

    repo.getPullRequest(pullNumber).let { pr ->
        pr.comments.firstOrNull { it.body.contains(baseMessage) }
            ?.update(stringToComment) ?: pr.comment(stringToComment)
    }
}

fun getRepository(githubRepoInfo: String, githubToken: String): GHRepository =
    GitHubBuilder()
        .withConnector(OkHttpConnector(OkHttpClient()))
        .withOAuthToken(githubToken)
        .build()
        .getRepository(githubRepoInfo)

doWork()
