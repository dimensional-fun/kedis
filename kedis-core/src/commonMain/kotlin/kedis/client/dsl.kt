package kedis.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kedis.client.command.RedisCommand
import kedis.client.command.auth
import kedis.client.command.type.RedisTypeReader
import kedis.protocol.Protocol
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public suspend fun RedisPool(
    uri: String,
    initialSize: Int = 5,
    maxSize: Int = 10,
    maxWaitTime: Long = 5000
): RedisPool = RedisPool(RedisURI(uri), initialSize, maxSize, maxWaitTime)

public suspend fun RedisPool(
    uri: RedisURI,
    initialSize: Int = 5,
    maxSize: Int = 10,
    maxWaitTime: Long = 5000
): RedisPool {
    require(initialSize > 0)
    require(initialSize <= maxSize) { "Initial size cannot be bigger than max size" }
    require(maxSize > 0)
    require(maxWaitTime > 0)

    val pool = RedisPool(uri, maxSize, maxWaitTime)

    repeat(initialSize) {
        val client = RedisClient(uri)
        pool.clients.add(client)
        pool.release(client)
    }

    return pool
}

public suspend fun RedisClient(uri: String, protocol: Protocol = Protocol.RESP2): RedisClient =
    RedisClient(RedisURI(uri), protocol)

public suspend fun RedisClient(uri: RedisURI, protocol: Protocol = Protocol.RESP2): RedisClient {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("Redis Client"))

    val socket = try {
        aSocket(SelectorManager(scope.coroutineContext))
            .tcp()
            .connect(uri.address)
    } catch (e: Exception) {
        RedisClient.log.error(e) { "Unable to connect to $uri" }
        throw e
    }

    RedisClient.log.debug { "Connected to $uri using protocol => $protocol" }

    val client = RedisClient(uri, protocol, scope, socket)
    if (protocol != Protocol.RESP2) {
        var hello = RedisCommand("HELLO", RedisTypeReader.Map, protocol.id.toString())
        if (uri.auth != null) {
            hello = hello.withOption("AUTH", uri.auth.username, uri.auth.password)
        }

        val info = client.executeCommand(hello)
        RedisClient.log.info { "Received HELLO -> $info" }
    } else if (uri.auth != null) {
        client.auth(uri.auth)
    }

    return client
}

@OptIn(ExperimentalContracts::class)
public inline fun RedisClient.pipelined(build: RedisPipeline.() -> Unit = {}): RedisPipeline {
    contract {
        callsInPlace(build, InvocationKind.EXACTLY_ONCE)
    }

    return RedisPipeline(this)
        .apply(build)
}
