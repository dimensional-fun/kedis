package mixtape.oss.kedis

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class RedisPool internal constructor(
    public val uri: RedisURI,
    private val maxSize: Int,
    private val maxWaitTime: Long
) {

    internal val clients = ConcurrentHashMap.newKeySet<RedisClient>(maxSize)

    private val channel = Channel<RedisClient>(Channel.UNLIMITED)
    private val mutex = Mutex()

    @Volatile
    private var closed = false

    private fun checkClosed() {
        check(!closed) { "RedisPool is closed" }
    }

    public val size: Int
        get() = clients.size

    public suspend fun get(): RedisClient {
        checkClosed()

        mutex.withLock {
            var client = withTimeoutOrNull(maxWaitTime) {
                channel.receive()
            }

            if (client != null) return client

            client = RedisClient(uri)
            clients.add(client)
            return client
        }
    }

    public suspend inline fun <T> use(block: (RedisClient) -> T): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        val client = get()
        try {
            return block(client)
        } finally {
            release(client)
        }
    }

    public suspend fun release(client: RedisClient) {
        checkClosed()
        require(clients.contains(client))

        if (clients.size > maxSize) {
            clients.remove(client)
            client.close()
        } else {
            channel.trySend(client)
        }
    }

    public suspend fun close() {
        if (this.closed) return

        synchronized(this) {
            if (this.closed) return
            this.closed = true
        }

        mutex.withLock {
            while (true) {
                channel.tryReceive().getOrNull()?.close() ?: break
            }

            channel.close()
        }
    }
}

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
