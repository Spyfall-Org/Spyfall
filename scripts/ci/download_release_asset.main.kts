#!/usr/bin/env kotlin

@file:DependsOn("org.kohsuke:github-api:1.125")

import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.net.URL

@Suppress("MagicNumber")
fun main() {
    val githubRepoInfo = args[0] // in the format: "REPO_OWNER/REPO_NAME"
    val githubToken = args[1]
    val tagName = args[2]
    val outPutEnvFile = File(args[3])
    val outKey = args[4]

    val aabContentType = "application/vnd.android.package-archive"
    val repo = getRepository(githubRepoInfo, githubToken)
    val release = repo.getReleaseByTagName(tagName)

    val releaseAsset = release.listAssets().firstOrNull {
        it.contentType == aabContentType && it.name.contains("release")
    }

    check(releaseAsset != null) { "No aab asset could found in release with tag name $tagName" }

    val url = URL(releaseAsset.browserDownloadUrl)
    val inputStream = url.openStream()
    val file = File(releaseAsset.name)
    val outputStream = FileOutputStream(file)
    inputStream.use { it.copyTo(outputStream) }
    outputStream.close()

    FileWriter(outPutEnvFile, true).let {
        it.write("$outKey=${file.path}")
        it.close()
    }
}

fun getRepository(githubRepoInfo: String, githubToken: String): GHRepository =
    GitHub.connectUsingOAuth(githubToken).getRepository(githubRepoInfo)

main()
