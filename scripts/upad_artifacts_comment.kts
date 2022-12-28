//
//@file:DependsOn("com.squareup.okhttp3:okhttp:4.9.0")
//
//import okhttp3.MediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody
//import org.json.JSONArray
//import org.json.JSONObject
//
//
//fun main(args: Array<String>) {
//    if (args.size != 2) {
//        println("Usage: comment_on_pr access_token workflow_id")
//        return
//    }
//    val accessToken = args[0]
//    val workflowId = args[1]
//
//    // Replace these values with your own
//    val repoOwner = "your_repo_owner"
//    val repoName = "your_repo_name"
//    val pullRequestNumber = 123
//
//    val client = OkHttpClient()
//
//    // First, get a list of all comments on the pull request
//    val request = Request.Builder()
//        .url("https://api.github.com/repos/$repoOwner/$repoName/issues/$pullRequestNumber/comments")
//        .addHeader("Authorization", "Bearer $accessToken")
//        .build()
//    val response = client.newCall(request).execute()
//    val responseBody = response.body?.string() ?: ""
//    val comments = JSONArray(responseBody)
//
//    // Find the comment with the text "Testing123"
//    var commentId: Int? = null
//    for (i in 0 until comments.length()) {
//        val commentObject = comments.getJSONObject(i)
//        if (commentObject.getString("body") == "Testing123") {
//            commentId = commentObject.getInt("id")
//            break
//        }
//    }
//
//    // Get the artifacts URL for the workflow
//    val artifactsUrl = getArtifactsUrl(accessToken, repoOwner, repoName, workflowId)
//
//    // If the comment was found, update it
//    if (commentId != null) {
//        val mediaType = MediaType.get("application/json; charset=utf-8")
//        val body = RequestBody.create(mediaType, """
//            {
//                "body": "$artifactsUrl"
//            }
//        """.trimIndent())
//        val updateRequest = Request.Builder()
//            .url("https://api.github.com/repos/$repoOwner/$repoName/comments/$commentId")
//            .patch(body)
//            .addHeader("Authorization", "Bearer $accessToken")
//            .build()
//        val updateResponse = client.newCall(updateRequest).execute()
//        println(updateResponse.body?.string())
//    } else {
//        // If the comment was not found, create a new one
//        val mediaType = MediaType.get("application/json; charset=utf-8")
//        val body = RequestBody.create(mediaType, """
//            {
//                "body": "$artifactsUrl"
//            }
//        """.trimIndent())
//        val createRequest = Request.Builder()
//            .url("https://api.github.com/repos/$repoOwner/$repoName/issues)
//    }
//}
