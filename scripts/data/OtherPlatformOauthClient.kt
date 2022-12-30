@Suppress("ConstructorParameterNaming")
data class OtherPlatformOauthClient(
    val client_id: String,
    val client_type: Int,
    val ios_info: IosInfo
)
