#!/usr/bin/env kotlin

@file:DependsOn("org.kohsuke:github-api:1.319")

import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.net.URL

val red = "\u001b[31m"
val green = "\u001b[32m"
val reset = "\u001b[0m"

fun printRed(text: String) {
    println(red + text + reset)
}

fun printGreen(text: String) {
    println(green + text + reset)
}

@Suppress("MagicNumber")
fun main() {
    val githubRepoInfo = args[0] // in the format: "REPO_OWNER/REPO_NAME"
    val githubToken = args[1]
    val tagName = args[2]
    val outPutEnvFile = File(args[3])
    val outKey = args[4]

    val repo = getRepository(githubRepoInfo, githubToken)
    val release = repo.getReleaseByTagName(tagName)

    release.listAssets().forEach {
        printGreen("""
            Release asset: ${it.name}
            type: ${it.contentType}
        """.trimIndent())
    }

    val aabReleaseAsset = release.listAssets().firstOrNull {
        it.name.contains("release") && it.name.contains("aab")
    }

    val apkReleaseAsset = release.listAssets().firstOrNull {
       it.name.contains("release") && it.name.contains("apk")
    }

    val releaseAsset = aabReleaseAsset ?: apkReleaseAsset

    check(releaseAsset != null) {
        "No release asset could found in release with tag name $tagName"
    }


    val url = URL(releaseAsset.browserDownloadUrl)
    val inputStream = url.openStream()
    val file = File(releaseAsset.name)
    val outputStream = FileOutputStream(file)
    inputStream.use { it.copyTo(outputStream) }
    outputStream.close()

    printGreen("release asset downloaded: ${file.name}")

    FileWriter(outPutEnvFile, true).let {
        it.write("$outKey=${file.path}")
        it.close()
    }
}

fun getRepository(githubRepoInfo: String, githubToken: String): GHRepository =
    GitHub.connectUsingOAuth(githubToken).getRepository(githubRepoInfo)

main()
