@Suppress("ConstructorParameterNaming")
data class GoogleServices(
    val client: List<Client>,
    val configuration_version: String,
    val project_info: ProjectInfo
)
