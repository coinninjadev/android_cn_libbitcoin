package app.coinninja.cn.libbitcoin

import app.coinninja.cn.libbitcoin.enum.Network
import app.coinninja.cn.libbitcoin.model.*
import java.security.SecureRandom

open class HDWallet @JvmOverloads constructor(words: Array<String> = emptyArray(), val network: Network = Network.MAINNET) {


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

    open fun encryptionKeys(uncompressedPublicKey: ByteArray): EncryptionKeys =
        encryptionKeys(
            SecureRandom().generateSeed(encryptionKeysEntropySize),
            uncompressedPublicKey
        )

    open fun decryptionKeys(derivationPath: DerivationPath, decoded: ByteArray): DecryptionKeys =
        decryptionKeys(key, network.which, derivationPath, decoded)

    open fun base58encodedKey(): String = base58encode(key, network.which)

    open fun transactionFrom(transactionData: TransactionData): Transaction =
        transactionFrom(key, network.which, transactionData)

    private external fun getVerificationKey(key: ByteArray, network: Int): String
    private external fun getSigningKey(key: ByteArray, network: Int): String
    private external fun newWords(entropy: ByteArray): Array<String>
    private external fun getAddressFor(key: ByteArray, network: Int, path: DerivationPath): MetaAddress
    private external fun privateKeyFor(words: Array<String>, network: Int): ByteArray
    private external fun sign(key: ByteArray, network: Int, data: ByteArray): String
    private external fun encryptionKeys(entropy: ByteArray, publicKey: ByteArray): EncryptionKeys
    private external fun decryptionKeys(
        key: ByteArray, network: Int,
        path: DerivationPath, publicKey: ByteArray
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
        private const val encryptionKeysEntropySize = 32
        fun generateNewWords(): Array<String> = HDWallet().newWords(SecureRandom.getSeed(16))
    }

}
