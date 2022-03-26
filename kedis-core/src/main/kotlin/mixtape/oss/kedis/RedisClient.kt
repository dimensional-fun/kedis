package mixtape.oss.kedis

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mixtape.oss.kedis.command.RedisCommand
import mu.KotlinLogging

internal val log = KotlinLogging.logger {  }

public interface RedisClient {

    public val uri: RedisURI

    public suspend fun <T> sendCommand(command: RedisCommand<T>): T?

    public suspend fun close()

    public suspend fun authenticate(auth: RedisAuth)

}

public suspend fun RedisClient(uri: String): RedisClient =
    RedisClient(RedisURI(uri))

public suspend fun RedisClient(uri: RedisURI): RedisClient {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("Redis Client"))

    val socket = try {
        aSocket(ActorSelectorManager(scope.coroutineContext))
            .tcp()
            .connect(uri.address)
    } catch (e: Exception) {
        log.error(e) { "Unable to connect to $uri" }
        throw e
    }

    log.info { "Connected to $uri" }

    val client = RedisClientImpl(uri, scope, socket)
    if (uri.auth != null) {
        client.authenticate(uri.auth)
    }

    return client
}
