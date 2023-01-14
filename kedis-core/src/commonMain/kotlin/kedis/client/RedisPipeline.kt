package kedis.client

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.withLock
import kedis.annotations.KedisInternalApi
import kedis.client.command.RedisCommand
import kedis.exception.RedisProtocolException
import kedis.tools.escaped
import naibu.logging.logging

@OptIn(KedisInternalApi::class)
public data class RedisPipeline(public val client: RedisClient) {
    public companion object {
        private val log by logging {  }
    }

    public val requests: MutableList<Request<*>> = mutableListOf()

    public fun <T> append(command: RedisCommand<T>): Request<T> {
        val request = Request(command, CompletableDeferred())
        requests.add(request)

        return request
    }

    public operator fun <T> RedisCommand<T>.unaryPlus(): Request<T> {
        return append(this)
    }

    public suspend fun execute(): List<Request<*>> {
        require(requests.isNotEmpty()) {
            "No commands were added"
        }

        client.mutex.withLock {
            val payload = requests.fold(byteArrayOf()) { a, b ->
                a + b.command.write(client.protocol.writer)
            }

            log.trace { "Executing pipeline with ${requests.size} commands -> ${payload.decodeToString().escaped}" }

            /* send and read pipeline request */
            client.sendPacket(payload)
            client.incoming.awaitContent()

            for (request in requests) {
                val deferred = (request.response as CompletableDeferred<Any?>)

                try {
                    val reply = client.readReply(request.command.reader)
                    deferred.complete(reply)
                } catch (ex: RedisProtocolException) {
                    deferred.completeExceptionally(ex)
                }
            }
        }

        return requests.toList()
    }

    public data class Request<T>(val command: RedisCommand<T>, val response: CompletableDeferred<T?>)
}
