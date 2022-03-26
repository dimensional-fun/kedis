package mixtape.oss.kedis

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class RedisPool(public val uri: RedisURI, initialSize: Int = 5) {
    public constructor(uri: String, initialSize: Int = 5) : this(RedisURI(uri), initialSize)

    private var semaphore: Semaphore = Semaphore(initialSize)

    private val usedClients: MutableList<RedisClient> = mutableListOf()

    private val clients: MutableList<RedisClient> = MutableList(initialSize) {
        runBlocking { RedisClient(uri) }
    }

    public val size: Int
        get() = usedClients.size + clients.size

    public suspend fun get(): RedisClient {
        var client = withTimeoutOrNull(5_000) {
            semaphore.acquire()
            clients.removeLast()
        }

        if (client == null) {
            client = RedisClient(uri)
            semaphore = Semaphore(size + 1, size - semaphore.availablePermits)
        }

        usedClients.add(client)
        return client
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

    public fun release(client: RedisClient): Boolean {
        if (usedClients.remove(client)) {
            return false
        }

        clients.add(client)
        semaphore.release()

        return true
    }

    public suspend fun close() {
        usedClients.forEach(::release)
        for (client in clients) {
            client.close()
        }

        clients.clear()
    }
}
