@Suppress("ConstructorParameterNaming")
data class Client(
    val api_key: List<ApiKey>,
    val client_info: ClientInfo,
    val oauth_client: List<OauthClient>,
    val services: Services
)
