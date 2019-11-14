package app.coinninja.cn.libbitcoin

import app.coinninja.cn.libbitcoin.enum.Network
import app.coinninja.cn.libbitcoin.model.*
import app.coinninja.cn.libbitcoin.util.hexToBytes
import java.security.SecureRandom

open class HDWallet @JvmOverloads constructor(
    words: Array<String> = emptyArray(),
    val network: Network = Network.MAINNET,
    val entropyGenerator: EntropyGenerator = EntropyGenerator()
) {

    internal var key: ByteArray

    init {
        System.loadLibrary("cnlibbitcoin")
        key = privateKeyFor(words, network.which)
    }

    open val signingKey: String get() = getSigningKey(key, network.which)
    open val verificationKey: String get() = getVerificationKey(key, network.which)

    open fun getAddressForPath(path: DerivationPath): MetaAddress =
        getAddressFor(key, network.which, path)

    open fun sign(data: String): String = sign(key, network.which, data.toByteArray())

    open fun fillBlock(
        purpose: Int, coin: Int, account: Int, chainIndex: Int,
        startingIndex: Int, bufferSize: Int
    ): Array<MetaAddress> {
        var index = startingIndex
        val block = mutableListOf<MetaAddress>()
        if (chainIndex == EXTERNAL || chainIndex == INTERNAL) {
            for (i in 0 until bufferSize) {
                block.add(
                    getAddressForPath(DerivationPath(purpose, coin, account, chainIndex, index))
                )
                index++
            }
        }

        return block.toTypedArray()
    }

    open fun encryptionKeys(uncompressedPublicKey: String): EncryptionKeys =
        encryptionKeys(
            entropyGenerator.generateEntropy(encryptionKeysEntropySize),
            uncompressedPublicKey.hexToBytes()
        )

    open fun encryptionKeysForM42(publicKey: ByteArray): EncryptionKeys =
        encryptionKeysForM42(key, network.which, publicKey)

    open external fun encryptionKeysForM42(
        key: ByteArray,
        network: Int,
        publicKey: ByteArray
    ): EncryptionKeys

    open fun decryptionKeys(derivationPath: DerivationPath, decoded: ByteArray): DecryptionKeys =
        decryptionKeys(key, network.which, derivationPath, decoded)

    open fun decryptionKeysForM42(publicKey: ByteArray): DecryptionKeys =
        decryptionKeysForM42(key, network.which, publicKey)

    open fun base58encodedKey(): String = base58encode(key, network.which)

    open fun transactionFrom(transactionData: TransactionData): Transaction =
        transactionFrom(key, network.which, transactionData)

    internal fun newWordsWithChecksumRetry(): Array<String> {
        var words = emptyArray<String>()

        do {
            words = newWords(SecureRandom.getSeed(16))
        } while (words.size != 12)

        return words
    }

    external fun newWords(entropy: ByteArray): Array<String>
    private external fun getVerificationKey(key: ByteArray, network: Int): String
    private external fun getSigningKey(key: ByteArray, network: Int): String
    private external fun getAddressFor(
        key: ByteArray,
        network: Int,
        path: DerivationPath
    ): MetaAddress

    private external fun privateKeyFor(words: Array<String>, network: Int): ByteArray
    private external fun sign(key: ByteArray, network: Int, data: ByteArray): String
    private external fun encryptionKeys(entropy: ByteArray, publicKey: ByteArray): EncryptionKeys
    private external fun decryptionKeys(
        key: ByteArray,
        network: Int,
        path: DerivationPath,
        publicKey: ByteArray
    ): DecryptionKeys

    private external fun decryptionKeysForM42(
        key: ByteArray,
        network: Int,
        publicKey: ByteArray
    ): DecryptionKeys

    private external fun base58encode(key: ByteArray, network: Int): String
    private external fun transactionFrom(
        key: ByteArray,
        network: Int,
        transactionData: TransactionData
    ): Transaction

    companion object {
        const val EXTERNAL = 0
        const val INTERNAL = 1
        private const val encryptionKeysEntropySize = 16

        fun generateNewWords(): Array<String> = HDWallet().newWordsWithChecksumRetry()
    }

}
