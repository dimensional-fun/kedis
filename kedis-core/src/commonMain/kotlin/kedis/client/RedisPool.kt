package kedis.client

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import naibu.concurrency.ConcurrentHashSet
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.Volatile

@OptIn(InternalCoroutinesApi::class, ExperimentalContracts::class)
public class RedisPool internal constructor(
    public val uri: RedisURI,
    private val maxSize: Int,
    private val maxWaitTime: Long
) : SynchronizedObject() {

    internal val clients = ConcurrentHashSet<RedisClient>(maxSize)

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
