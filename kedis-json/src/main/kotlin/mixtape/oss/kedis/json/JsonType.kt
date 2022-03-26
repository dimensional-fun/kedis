package mixtape.oss.kedis.json

public enum class JsonType {
    NULL,
    BOOLEAN,
    INTEGER,
    NUMBER,
    STRING,
    OBJECT,
    ARRAY;

    public companion object {
        public fun find(raw: String): JsonType? = values().find { it.name.equals(raw, true) }
    }
}
