package mixtape.oss.kedis

public data class RedisAuth(val username: String?, val password: String) {
    public val asString: String = "${username ?: ""}:$password"

    init {
        require(password.isNotBlank()) { "Password must not blank" }
        if (username != null) {
            require(username.isNotBlank()) { "Username must not blank" }
        }
    }
}
