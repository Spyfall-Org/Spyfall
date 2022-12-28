#!/usr/bin/env kotlin

@file:DependsOn("org.kohsuke:github-api:1.125")

import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub

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
        
        Usage: ./update_artifacts_comment.main.kts [GITHUB_REPO] [GITHUB_TOKEN] [PULL_NUMBER] [RUN_ID]
        
        [GITHUB_REPO] - REPO_OWNER/REPO_NAME, provided by github actions as env variable
        [GITHUB_TOKEN] - token to interact with github provided by github actions as env variable or use PAT
        [PULL_NUMBER] - the number of the pull request
        [RUN_ID] - the number uniquely associated with this workflow run. Used to get artifacts url. 
        
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

    val repo = getRepository(githubRepoInfo, githubToken)

    val artifactsUrl = repo.getWorkflowRun(runID.toLong()).artifactsUrl
    val url = repo.getWorkflowRun(runID.toLong()).url
    val workflowUrl = repo.getWorkflowRun(runID.toLong()).workflowUrl
    val jobsUrl = repo.getWorkflowRun(runID.toLong()).jobsUrl
    val htmlUrl = repo.getWorkflowRun(runID.toLong()).htmlUrl


    val baseMessage = "Automated PR Artifacts Links"

    val lastCommitSha = repo.getPullRequest(pullNumber.toInt()).head.sha.take(6)

    val existingComment = repo
        .getPullRequest(pullNumber.toInt())
        .comments.firstOrNull { it.body.contains(baseMessage) }
        ?.body

    val updatedComment = (existingComment ?: baseMessage) + """
        
        $lastCommitSha : $artifactsUrl
        $lastCommitSha : $url
        $lastCommitSha : $workflowUrl
        $lastCommitSha : $jobsUrl
        $lastCommitSha : $htmlUrl

        
        
    """.trimIndent()

    repo.getPullRequest(pullNumber.toInt()).let { pr ->
        pr.comments.firstOrNull { it.body.contains(baseMessage) }
            ?.update(updatedComment) ?: pr.comment(updatedComment)

    }
}

fun getRepository(githubRepoInfo: String, githubToken: String): GHRepository =
    GitHub.connectUsingOAuth(githubToken).getRepository(githubRepoInfo)


doWork()
