package kedis.client

public data class RedisAuth(val username: String, val password: String) {
    public val asString: String = "$username:$password"
}
