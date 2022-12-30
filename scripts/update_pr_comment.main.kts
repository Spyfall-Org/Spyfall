#!/usr/bin/env kotlin

@file:Import("util/GithubActionsUtil.main.kts")

@file:DependsOn("org.kohsuke:github-api:1.125")

import org.kohsuke.github.GHRelease
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.File

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
        
        Usage: ./update_pr_comment.main.kts [GITHUB_REPO] [GITHUB_TOKEN] [PULL_NUMBER] [RUN_ID] [SPYFALL_FIREBASE_LINK] [WEREWOLF_FIREBASE_LINK]
        
        [GITHUB_REPO] - REPO_OWNER/REPO_NAME, provided by github actions as env variable
        [GITHUB_TOKEN] - token to interact with github provided by github actions as env variable or use PAT
        [PULL_NUMBER] - the number of the pull request
        [RUN_ID] - the number uniquely associated with this workflow run. Used to get artifacts url.
        [SPYFALL_FIREBASE_LINK] - Link to the firebase release for this build
        [WEREWOLF_FIREBASE_LINK] - Link to the firebase release for this build
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
    val spyfallFirebaseLink = args[4]
    val werewolfFirebaseLink = args[5]
    val tagName = args.getOrNull(5)

    val repo = getRepository(githubRepoInfo, githubToken)

    val releaseDraft = repo.listReleases().firstOrNull { it.isDraft && it.tagName == tagName }

    updatePRArtifactsComment(
        repo,
        runID.toLong(),
        pullNumber.toInt(),
        releaseDraft,
        spyfallFirebaseLink,
        werewolfFirebaseLink
    )
}

@Suppress("LongParameterList")
fun updatePRArtifactsComment(
    repo: GHRepository,
    runID: Long,
    pullNumber: Int,
    releaseDraft: GHRelease?,
    spyfallFirebaseDistributionLink: String,
    werewolfFirebaseDistributionLink: String,
    ) {
    val htmlUrl = repo.getWorkflowRun(runID).htmlUrl

    val firebaseLinksMd = listOf(
        "Spyfall Firebase Distribution Link" to spyfallFirebaseDistributionLink,
        "Werewolf Firebase Distribution Link" to werewolfFirebaseDistributionLink
    )
        .map { (linkText, linkValue) -> "[$linkText]($linkValue)" }
        .fold("") { linkA: String, linkB: String -> "$linkA | $linkB" }

    @Suppress("MaxLineLength")
    val baseMessage = """
        # Automated PR Assets Links
        These assets are automatically generated on pull requests. Some links may not work until all jobs in the pull request workflow have finished. Every update to this PR will generate a new row in the assets table. 
        
        ${
        (if (releaseDraft != null) """
            The draft for this release can be found [here](${releaseDraft.htmlUrl}). When it is time to release, publish
            the draft release and merge this PR. See the [release documentation](https://spyfall-org.github.io/how-to/release/) for more info. 
        """.trimIndent() else null) ?: ""
        }

        | Commit | Assets  | 
        |---|---|
        """.trimIndent()

    @Suppress("MagicNumber")
    val lastCommitSha = repo.getPullRequest(pullNumber).head.sha.take(7)

    val existingComment = repo
        .getPullRequest(pullNumber)
        .comments.firstOrNull { it.body.contains(baseMessage) }
        ?.body

    val assetsTableEntry = """
        |$lastCommitSha |  [Github Action Artifacts]($htmlUrl#artifacts) $firebaseLinksMd |
    """.trimIndent()

    val stringToComment = if (existingComment != null) {
        "$existingComment\n$assetsTableEntry"
    } else {
        "$baseMessage\n$assetsTableEntry"
    }

    repo.getPullRequest(pullNumber).let { pr ->
        pr.comments.firstOrNull { it.body.contains(baseMessage) }
            ?.update(stringToComment) ?: pr.comment(stringToComment)
    }
}

fun getRepository(githubRepoInfo: String, githubToken: String): GHRepository =
    GitHub.connectUsingOAuth(githubToken).getRepository(githubRepoInfo)

doWork()
