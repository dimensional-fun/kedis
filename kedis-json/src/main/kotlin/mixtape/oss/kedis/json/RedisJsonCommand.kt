package mixtape.oss.kedis.json

import mixtape.oss.kedis.protocol.RedisProtocolCommand

public enum class RedisJsonCommand(override val literal: String) : RedisProtocolCommand {
    /* SCALAR COMMANDS */
    SET("JSON.SET"),
    GET("JSON.GET"),
    MGET("JSON.MGET"),
    DEL("JSON.DEL"),
    CLEAR("JSON.CLEAR"),
    NUM_INCR_BY("JSON.NUMINCRBY"),
    NUM_MULT_BY("JSON.NUMMULTBY"),
    TOGGLE("JSON.TOGGLE"),
    STR_APPEND("JSON.STRAPPEND"),
    STR_LEN("JSON.STRLEN"),

    /* ARRAY COMMANDS */
    ARR_APPEND("JSON.ARRAPPEND"),
    ARR_INDEX("JSON.ARRINDEX"),
    ARR_INSERT("JSON.ARRINSERT"),
    ARR_LEN("JSON.ARRLEN"),
    ARR_POP("JSON.ARRPOP"),
    ARR_TRIM("JSON.ARRTRIM"),

    /* DEBUG COMMANDS */
    TYPE("JSON.TYPE");
}
