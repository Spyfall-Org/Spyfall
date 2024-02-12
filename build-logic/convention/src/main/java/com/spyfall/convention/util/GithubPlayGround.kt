package com.spyfall.convention.util

import okhttp3.OkHttpClient

import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.extras.okhttp3.OkHttpConnector

fun main() {
    println("Hello, world!")

    GitHubBuilder()
        .withConnector(OkHttpConnector(OkHttpClient()))
        .build()

    GitHubBuilder()
        .withConnector(OkHttpConnector(OkHttpClient()))
        .withOAuthToken("token")
        .build()

}
