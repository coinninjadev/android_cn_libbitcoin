package app.coinninja.cn.libbitcoin

import app.coinninja.cn.libbitcoin.enum.Network
import app.coinninja.cn.libbitcoin.enum.ReplaceableOption
import app.coinninja.cn.libbitcoin.model.*
import app.coinninja.cn.libbitcoin.util.hexToBytes
import app.coinninja.cn.libbitcoin.util.toHexString
import com.google.common.truth.Truth.assertThat
import org.junit.Ignore
import org.junit.Test
import java.util.*


class HDWalletTest {
    @Test
    fun two_instances_different_words_function_togeather() {
        val path = DerivationPath(49, 0, 0, 0, 0)
        val walletA = HDWallet(words)
        val walletB = HDWallet(_words)
        assertThat(walletB.getAddressForPath(path).address).isEqualTo("3Fx121K9uGzPbWme55ea3b4Hswu6GVPjHu")
        assertThat(walletA.getAddressForPath(path).address).isEqualTo("37VucYSaXLCAsxYyAPfbSi9eh4iEcbShgf")
    }

    // SEED Word generation

    @Test
    fun generates_words() {
        assertThat(HDWallet.generateNewWords().size).isEqualTo(12)
    }

    // Private Key
    @Test
    fun private_key_from_words__mainnet() {
        val hdWallet = HDWallet(words)
        assertThat(hdWallet.base58encodedKey())
            .isEqualTo("xprv9s21ZrQH143K3GJpoapnV8SFfukcVBSfeCficPSGfubmSFDxo1kuHnLisriDvSnRRuL2Qrg5ggqHKNVpxR86QEC8w35uxmGoggxtQTPvfUu")
    }

    @Test
    fun private_key_from_words__testnet() {
        val hdWallet = HDWallet(words, Network.TESTNET)
        assertThat(hdWallet.base58encodedKey())
            .isEqualTo("tprv8ZgxMBicQKsPe5YMU9gHen4Ez3ApihUfykaqUorj9t6FDqy3nP6eoXiAo2ssvpAjoLroQxHqr3R5nE3a5dU3DHTjTgJDd7zrbniJr6nrCzd")
    }

    // Signing / Verification

    @Test
    fun provides_signing_key() {
        assertThat(HDWallet(words).signingKey)
            .isEqualTo("xprv9ukW2UsmeQPBDWViiTd81WaQHCm66iHgkoMtBeLEqnPyBhs5tvP54ee4FNRacQcCLkHtgxQx7BobhU3vpmCctRwXu8YLdnEZ2y4L32VCAmN")
    }

    @Test
    fun provides_verification_key() {
        assertThat(HDWallet(words).verificationKey)
            .isEqualTo("024458596b5c97e716e82015a72c37b5d3fe0c5dc70a4b83d72e7d2eb65920633e")
    }

    @Test
    fun sign_data() {
        val expected =
            "3045022100c515fc2ed70810f6b1383cfe8e81b9b41b08682511e92d557f1b1719391b521d02200d9d734fd09ce60586ac48b0a7eb587a50958cd9fa548ffa39088fc6ada12eec"

        assertThat(HDWallet(words).sign("Hello World")).isEqualTo(expected)
    }

    // Encryption / Decryption Keys
    @Test
    fun test_getEncryptionKeys() {
        val wallet = HDWallet(words)
        val uncompressedPublicKey = "04904240a0aaec6af6f9b6c331f71feea2a4ed1549c06e5a6409fe92c5824dc4c54e26c2b2e27cfc224a6b782b35a2872b666f568cf37456262fbb065601b4d73a"

        val keys1: EncryptionKeys = wallet.encryptionKeys(uncompressedPublicKey)
        assertThat(keys1.encryptionKey.size).isEqualTo(32)
        assertThat(keys1.hmacKey.size).isEqualTo(32)
        assertThat(keys1.associatedPublicKey.size).isEqualTo(65)
        assertThat(keys1.associatedPublicKey[0]).isEqualTo("4".toByte())

        val keys2 = wallet.encryptionKeys(uncompressedPublicKey)
        assertThat(keys1.encryptionKey).isNotEqualTo(keys2.encryptionKey)
        assertThat(keys1.hmacKey).isNotEqualTo(keys2.hmacKey)
        assertThat(keys1.associatedPublicKey).isNotEqualTo(keys2.associatedPublicKey)
    }

    @Test
    fun test_getDecryptionKeys() {
        val wallet = HDWallet(words)
        val derivationPath = DerivationPath(49, 0, 0, 0, 0)
        val decoded = Base64.getDecoder()
            .decode("BBS6AnMOS9Y+uGsEDYQycHHzcC7PPmzuKDtSda842AtSANZjgm++vr8uEc/bWacKQDL+/KyL3CuIs+m+ueejbBs=")
        val keys: DecryptionKeys = wallet.decryptionKeys(derivationPath, decoded)
        assertThat(keys.encryptionKey.toHexString()).isEqualTo("c57f563d3dab750e56e96a8c77b2503acb0ac0cea47ca8863dd370f5988775e2")
        assertThat(keys.hmacKey.toHexString()).isEqualTo("3726b40481842beddd888df1b60afc693d3334781c74664c1469a8b908d1d16f")
    }

    @Test
    fun test_encryption_and_decryption_keys_are_compatible() {
        val wallet = HDWallet(words)
        //get a receiver uncompressed public key for derivationPath
        val derivationPath = DerivationPath(49, 0, 0, 0, 0)
        val uncompressedPublicKey = wallet.getAddressForPath(derivationPath).pubKey

        //sender builds encryption keys
        val encryptionKeys: EncryptionKeys =
            wallet.encryptionKeys(uncompressedPublicKey)

        //receiver builds decryption keys from ephemeral public key and derivationPath
        val decryptionKeys: DecryptionKeys =
            wallet.decryptionKeys(derivationPath, encryptionKeys.associatedPublicKey)

        //assert both encryptionKeys and hmacKeys are the same
        assertThat(encryptionKeys.encryptionKey).isEqualTo(decryptionKeys.encryptionKey)
        assertThat(encryptionKeys.hmacKey).isEqualTo(decryptionKeys.hmacKey)
    }

    // Address generation

    @Test
    fun generates_address_from_path_test_net_purpose_49() {
        val path = DerivationPath(49, 1, 0, 0, 0)
        val metaAddress = HDWallet(words, network = Network.TESTNET).getAddressForPath(path)
        assertThat(metaAddress.address).isEqualTo("2Mww8dCYPUpKHofjgcXcBCEGmniw9CoaiD2")
        assertThat(metaAddress.pubKey).isEqualTo("04a1af804ac108a8a51782198c2d034b28bf90c8803f5a53f76276fa69a4eae77f3010ba699877871e188285d8c36e320eb08311d8aecf27ff8971bc7fde240bfd")
        assertThat(metaAddress.derivationPath).isEqualTo(path)
    }

    @Test
    fun generates_address_from_path_main_net_purpose_49() {
        val path = DerivationPath(49, 0, 0, 0, 0)
        val metaAddress = HDWallet(words).getAddressForPath(path)
        assertThat(metaAddress.address).isEqualTo("37VucYSaXLCAsxYyAPfbSi9eh4iEcbShgf")
        assertThat(metaAddress.pubKey).isEqualTo("049b3b694b8fc5b5e07fb069c783cac754f5d38c3e08bed1960e31fdb1dda35c2449bdd1f0ae7d37a04991d4f5927efd359c13189437d9eae0faf7d003ffd04c89")
        assertThat(metaAddress.derivationPath).isEqualTo(path)
    }

    @Test
    fun generates_address_from_path_test_net_purpose_84_chain_external() {
        val path = DerivationPath(84, 1, 0, 0, 0)
        val metaAddress = HDWallet(words, network = Network.TESTNET).getAddressForPath(path)
        assertThat(metaAddress.address).isEqualTo("bcrt1q6rz28mcfaxtmd6v789l9rrlrusdprr9pz3cppk")
        assertThat(metaAddress.pubKey).isEqualTo("04e7ab2537b5d49e970309aae06e9e49f36ce1c9febbd44ec8e0d1cca0b4f9c3196a8177238417b530160bf7ce4e02b931c9063d56be2efbc7d3d08b745224e928")
        assertThat(metaAddress.derivationPath).isEqualTo(path)
    }

    @Test
    fun generates_address_from_path_main_net_purpose_84_chain_external() {
        val path = DerivationPath(84, 0, 0, 0, 0)
        val metaAddress = HDWallet(words).getAddressForPath(path)
        assertThat(metaAddress.address).isEqualTo("bc1qcr8te4kr609gcawutmrza0j4xv80jy8z306fyu")
        assertThat(metaAddress.pubKey).isEqualTo("0430d54fd0dd420a6e5f8d3624f5f3482cae350f79d5f0753bf5beef9c2d91af3c04717159ce0828a7f686c2c7510b7aa7d4c685ebc2051642ccbebc7099e2f679")
        assertThat(metaAddress.derivationPath).isEqualTo(path)
    }

    @Test
    fun generates_address_from_path_test_net_purpose_84_chain_internal() {
        val path = DerivationPath(84, 1, 0, 1, 0)
        val metaAddress = HDWallet(words, network = Network.TESTNET).getAddressForPath(path)
        assertThat(metaAddress.address).isEqualTo("bcrt1q9u62588spffmq4dzjxsr5l297znf3z6jkgnhsw")
        assertThat(metaAddress.pubKey).isEqualTo("")
        assertThat(metaAddress.derivationPath).isEqualTo(path)
    }

    @Test
    fun generates_address_from_path_main_net_purpose_84_chain_internal() {
        val path = DerivationPath(84, 0, 0, 1, 0)
        val metaAddress = HDWallet(words).getAddressForPath(path)
        assertThat(metaAddress.address).isEqualTo("bc1q8c6fshw2dlwun7ekn9qwf37cu2rn755upcp6el")
        assertThat(metaAddress.pubKey).isEqualTo("")
        assertThat(metaAddress.derivationPath).isEqualTo(path)
    }

    @Test
    fun fills_blocks_of_addresses__INTERNAL() {
        val wallet = HDWallet(words)
        val block = wallet.fillBlock(49, 0, 0, 1, 0, 20)
        assertThat(block[0].address).isEqualTo("34K56kSjgUCUSD8GTtuF7c9Zzwokbs6uZ7")
        assertThat(block[1].address).isEqualTo("3516F2wmK51jVRrggEJsTUBNWMSLLjzvJ2")
        assertThat(block[2].address).isEqualTo("3Grd7y95JEDTSh9uiVF5q7z2qGzmkP19CV")
        assertThat(block[3].address).isEqualTo("3NUH31YRjTtc7LVJwiouhqpYt26Nn6sM9z")
        assertThat(block[4].address).isEqualTo("3BEtJydjKS1FxBhjY2yX4qsxWtQFgSPxCr")
        assertThat(block[5].address).isEqualTo("3AvPKjZV3GMxVXPVo3oQzEd6MeisYacRmr")
        assertThat(block[6].address).isEqualTo("3NewDGLtkpyLnUcT4zNiatYP4vM4hQstR3")
        assertThat(block[7].address).isEqualTo("38V3znZWZwupqdNN7NCQF4VgjcVCcNHEM1")
        assertThat(block[8].address).isEqualTo("389Gz3BDnMbG4rUHd76ckNBHF6SuL6yTfk")
        assertThat(block[9].address).isEqualTo("3CuGpzkxtZgDLKMa9gqHnH2kb5rEagvgbw")
        assertThat(block[10].address).isEqualTo("38BhTc8HcgUwdsafFQqd2TSN8iFGgsLQZK")
        assertThat(block[11].address).isEqualTo("3EgkYjJd5fnLP3QUJk32HbbZaa5TeGLTFd")
        assertThat(block[12].address).isEqualTo("3JiJfm196KrMrxHmpmgUW6ViM9YNbyKqUq")
        assertThat(block[13].address).isEqualTo("3LTWVaQccRLNTkqsMFneh4DMXkLb1pqNnW")
        assertThat(block[14].address).isEqualTo("36Xg7a1kwfsXSVPWESsbykai7Czgmc9dVZ")
        assertThat(block[15].address).isEqualTo("3P1X3AdfcZj3Xi6TWhfAR6HJDMbibn3SwH")
        assertThat(block[16].address).isEqualTo("3Bcn11q9pfT6TFyTFGNZYtcFrvMyEhgiih")
        assertThat(block[17].address).isEqualTo("3JtLeovVcoRZBZYs6B7d7RmRevy8vYbmWp")
        assertThat(block[18].address).isEqualTo("3AWVov6vVA7Yzhy3BC8VSfqyS1DCyBjceb")
        assertThat(block[19].address).isEqualTo("3FHQ3NVAVXhjykV9NCH64HB6RR6EuLMsfJ")
    }

    @Test
    fun fills_blocks_of_addresses__EXTERNAL() {
        val wallet = HDWallet(words)
        val block = wallet.fillBlock(49, 0, 0, 0, 0, 20)
        assertThat(block[0].address).isEqualTo("37VucYSaXLCAsxYyAPfbSi9eh4iEcbShgf")
        assertThat(block[1].address).isEqualTo("3LtMnn87fqUeHBUG414p9CWwnoV6E2pNKS")
        assertThat(block[2].address).isEqualTo("3B4cvWGR8X6Xs8nvTxVUoMJV77E4f7oaia")
        assertThat(block[3].address).isEqualTo("38CahkVftQneLonbWtfWxiiaT2fdnzsEAN")
        assertThat(block[4].address).isEqualTo("37mbeJptxfQC6SNNLJ9a8efCY4BwBh5Kak")
        assertThat(block[5].address).isEqualTo("3QrMAP4ZG3a7Y1qFF5A4sY8MeSUxZ8Yxjy")
        assertThat(block[6].address).isEqualTo("3NzFBzVHKEVAnYKWXjZKJ3H4n4pUuq2sfg")
        assertThat(block[7].address).isEqualTo("3KHhcgwPgYF9hE77zaKy2G36dpkcNtvQ33")
        assertThat(block[8].address).isEqualTo("3LwcWnqXb6f371qkWZRxW9Hbe798zLmpAS")
        assertThat(block[9].address).isEqualTo("3HFZKZgRfzcEbu7ggo4BD9opSrjLAJWVWv")
        assertThat(block[10].address).isEqualTo("38mWd5D48ShYPJMZngtmxPQVYhQR5DGgfF")
        assertThat(block[11].address).isEqualTo("34HSx9QGfkGHupAdtRBpBNTiFHxEXpscdj")
        assertThat(block[12].address).isEqualTo("3HB1WEujyUJicjgKV4RiBMNRLoWYmLDr1s")
        assertThat(block[13].address).isEqualTo("35DhkaiFrp3oCBzxUnmut8nCbDnbykQMbC")
        assertThat(block[14].address).isEqualTo("3Drg3sRxhxEDtNi66pmEorZqRaWDgDLnHL")
        assertThat(block[15].address).isEqualTo("3MLaBHZRQBz6h2ADe6DfChSaZmfMYWBfJP")
        assertThat(block[16].address).isEqualTo("3NmSLfUSMB3zstyMRMzfFmkPXMufrhsuAc")
        assertThat(block[17].address).isEqualTo("327kJyGsgTixfKdSvK5JqfXbXQQrczhLE9")
        assertThat(block[18].address).isEqualTo("3EJ3YM6ELZ7f2GsARv7AMpJvHoeTqa9V93")
        assertThat(block[19].address).isEqualTo("3FsmoJ9P2eUrKjd2ooa8UJVAeyMVPNkvp2")
    }

    @Test
    fun transaction_from_transaction_data__no_utxos_yeilds_empty_transaction() {
        val transactionData = TransactionData(
            utxos = emptyArray(),
            amount = 89895996,
            changeAmount = 1000,
            feeAmount = 5176597,
            paymentAddress = "3516F2wmK51jVRrggEJsTUBNWMSLLjzvJ2",
            changePath = DerivationPath(49, 0, 0, 1, 0),
            replaceableOption = ReplaceableOption.MUST_NOT_BE_RBF
        )
        val hdWallet = HDWallet(words)

        val transaction: Transaction = hdWallet.transactionFrom(transactionData)

        assertThat(transaction).isNotNull()
        assertThat(transaction.txid).isEqualTo("")
        assertThat(transaction.encodedTransaction).isEqualTo("")

    }

    @Test
    fun transaction_from_transaction_data() {
        val transactionData = TransactionData(
            utxos = arrayOf(
                UnspentTransactionOutput(
                    txid = "f14914f76ad26e0c1aa5a68c82b021b854c93850fde12f8e3188c14be6dc384e",
                    index = 1,
                    amount = 33253,
                    path = DerivationPath(49, 0, 0, 1, 7),
                    replaceable = true
                )
            ),
            amount = 23147,
            changeAmount = 0,
            feeAmount = 10108,
            paymentAddress = "1HT6WtD5CAToc8wZdacCgY4XjJR4jV5Q5d",
            changePath = DerivationPath(49, 0, 0, 1, 2),
            replaceableOption = ReplaceableOption.MUST_NOT_BE_RBF,
            blockHeight = 500000
        )
        val hdWallet = HDWallet(words)

        val transaction: Transaction = hdWallet.transactionFrom(transactionData)

        assertThat(transaction).isNotNull()
        assertThat(transaction.txid).isEqualTo("77cf4bddf3d133fc37a08e18c47607702e0aec095606f364081d22a4680c3e97")
        assertThat(transaction.encodedTransaction).isEqualTo("010000000001014e38dce64bc188318e2fe1fd5038c954b821b0828ca6a51a0c6ed26af71449f10100000017160014b4381165b195b3286079d46eb2dc8058e6f02241ffffffff016b5a0000000000001976a914b4716e71b900b957e49f749c8432b910417788e888ac02483045022100f8a78ff2243c591ffb7af46ed670b173e5e5dd3f19853493f5c3bda85425f8ef02203d152fdc632388da527c4a58b796a8a40d1a9d15176d80dedfef96a38ecc9ae7012103a45ef894ab9e6f2e55683561181be9e69b20207af746d60b95fab33476dc932420a10700")
    }

    companion object {
        val words = arrayOf(
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "about"
        )

        val _words = arrayOf(
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "abandon"
        )
    }
}