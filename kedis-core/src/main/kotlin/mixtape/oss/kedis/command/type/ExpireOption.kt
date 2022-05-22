package mixtape.oss.kedis.command.type

import mixtape.oss.kedis.protocol.Rawable

/**
 * Used in the `EXPIRES` command.
 */
public enum class ExpireOption : Rawable {
    /**
     * NX – Set expiry only when the key has no expiry
     */
    NX,

    /**
     * XX – Set expiry only when the key has an existing expiry
     */
    XX,

    /**
     * Set expiry only when the new expiry is greater than current one
     */
    GT,

    /**
     * Set expiry only when the new expiry is less than current one
     */
    LT;

    override fun bytes(): ByteArray {
        return name.encodeToByteArray()
    }
}
