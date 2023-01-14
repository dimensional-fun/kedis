package kedis.client.command.type

import kedis.client.RedisClient
import kedis.protocol.RedisType
import kedis.protocol.asArray
import kedis.protocol.asInteger
import kedis.protocol.asText

public data class TrackingInfo(val flags: List<String>, val redirect: Long, val prefixes: List<String>) {
    public companion object : RedisTypeReader<TrackingInfo> {
        override suspend fun read(type: RedisType, client: RedisClient): TrackingInfo? {
            val sections = RedisTypeReader.Map.read(type, client)
                ?: return null

            return TrackingInfo(
                sections["flags"]!!.asArray()?.mapNotNull { it.asText() }!!,
                sections["redirect"]!!.asInteger()!!,
                sections["prefixes"]!!.asArray()?.mapNotNull { it.asText() }!!
            )
        }
    }
}
