@file:OptIn(KedisInternalApi::class, ExperimentalCoroutinesApi::class)

package mixtape.oss.kedis

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.sync.withLock
import mixtape.oss.kedis.annotations.KedisInternalApi
import mixtape.oss.kedis.command.RedisCommand
import mixtape.oss.kedis.exception.RedisProtocolException

public data class RedisPipeline(public val client: RedisClient) {
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
                a + b.command.bytes()
            }

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

    public data class Request<T>(val command: RedisCommand<T>, internal val response: CompletableDeferred<T?>)
}
