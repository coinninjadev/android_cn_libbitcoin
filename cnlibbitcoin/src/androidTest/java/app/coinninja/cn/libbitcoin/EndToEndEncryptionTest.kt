package app.coinninja.cn.libbitcoin

import android.util.Base64
import app.coinninja.cn.libbitcoin.model.DerivationPath
import app.coinninja.cn.libbitcoin.util.hexToBytes
import app.coinninja.cn.libbitcoin.util.toHexString
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito.mock

class EndToEndEncryptionTest {
    var messageCryptor = MessageCryptor()
    val encrypted =
        "AwDVbESpwTj3VllUN9l4igjkPk3pIUahlOF32v2sGhjX7m3vqos/LAnju3cPO8AR/GUTE9xpmqg/ED0IpsanFib2BBS6AnMOS9Y+uGsEDYQycHHzcC7PPmzuKDtSda842AtSANZjgm++vr8uEc/bWacKQDL+/KyL3CuIs+m+ueejbBs="
    val encryptionKeyBase64 = "rqcLXf0yHmSHIRaoXO2jnD+gVODrIil0w9DEDesbzdM="
    val hmacBase64 = "GwX848SPaO3qnOpor6BNRMMe+Y8h3RVDzZfQjgUzP/Y="
    val ephemeralPublicKeyBase64 =
        "BBS6AnMOS9Y+uGsEDYQycHHzcC7PPmzuKDtSda842AtSANZjgm++vr8uEc/bWacKQDL+/KyL3CuIs+m+ueejbBs="


    @Test
    fun endToEnd() {
        val entropy = "d577c688dff829903284f5251b7681c91"
        val entropyGenerator: EntropyGenerator = mock(EntropyGenerator::class.java)
        whenever(entropyGenerator.generateEntropy(16)).thenReturn(entropy.hexToBytes())
        val bobsWords = arrayOf(
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "about"
        )
        val aliceWords = arrayOf("word", "word", "word", "word", "word", "word", "word",
            "word", "word", "word", "word", "word")

        val alicesWallet = HDWallet(aliceWords, entropyGenerator = entropyGenerator)
        val bobsWallet = HDWallet(bobsWords, entropyGenerator = entropyGenerator)


        //get a receiver uncompressed public key for derivationPath
        val derivationPath = DerivationPath(49, 0, 0, 0, 0)
        val address = bobsWallet.getAddressForPath(derivationPath)
        assertThat(address.address).isEqualTo("37VucYSaXLCAsxYyAPfbSi9eh4iEcbShgf")
        assertThat(address.pubKey)
            .isEqualTo("049b3b694b8fc5b5e07fb069c783cac754f5d38c3e08bed1960e31fdb1dda35c2449bdd1f0ae7d37a04991d4f5927efd359c13189437d9eae0faf7d003ffd04c89")

        //sender builds encryption keys from rupk
        val encryptionKeys = alicesWallet.encryptionKeys(address.pubKey)

        val message = "Howdy Doody"
        val dataToEncrypt = message.toByteArray()

        val encryptedMessage = messageCryptor.encrypt(
            dataToEncrypt,
            encryptionKeys.encryptionKey,
            encryptionKeys.hmacKey,
            encryptionKeys.associatedPublicKey
        )

        // Sender can decrypt with their encryption keys
        assertThat(
            String(
                messageCryptor.decrypt(
                    encryptedMessage,
                    encryptionKey = encryptionKeys.encryptionKey,
                    hmac = encryptionKeys.hmacKey
                )
            )
        ).isEqualTo(message)

        // receiver extracts ephemeralPublicKey from the encrypted payload
        val ephemeralPublicKey = messageCryptor.unpackEphemeralPublicKey(encryptedMessage)
        assertThat(ephemeralPublicKey).isEqualTo(encryptionKeys.associatedPublicKey)

        //receiver builds decryption keys from ephemeral public key and derivationPath
        val decryptionKeys = bobsWallet.decryptionKeys(derivationPath, ephemeralPublicKey)

        val decryptedMessage = messageCryptor.decrypt(
            encryptedMessage,
            decryptionKeys.encryptionKey,
            decryptionKeys.hmacKey
        )

        assertThat(decryptedMessage).isEqualTo(dataToEncrypt)
    }

    @Test
    fun build_encryption() {
        // Allice
        val words = arrayOf(
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "about", "about"
        )

        val entropy = "d577c688dff829903284f5251b7681c9"
        assertThat(entropy.hexToBytes().size).isEqualTo(16)
        val entropyGenerator: EntropyGenerator = mock(EntropyGenerator::class.java)
        whenever(entropyGenerator.generateEntropy(any())).thenReturn(entropy.hexToBytes())
        val hdWallet = HDWallet(words, entropyGenerator = entropyGenerator)

        assertThat(hdWallet.entropyGenerator.generateEntropy(16).toHexString())
            .isEqualTo("d577c688dff829903284f5251b7681c9")

        //get a receiver uncompressed public key for derivationPath
        // Bobs m/49/0/0/0/0
        val publicKey =
            "049b3b694b8fc5b5e07fb069c783cac754f5d38c3e08bed1960e31fdb1dda35c2449bdd1f0ae7d37a04991d4f5927efd359c13189437d9eae0faf7d003ffd04c8"

        //publicKey = "048e836393a91c7437e7b199b8a7fd6f60fa942eb27772874945ab5639e2fa7bda6cae9f06eedd7d212c84500ce3aaaa44fe953aaa14a537466231975f1598ffc5"

        //sender builds encryption keys from rupk
        val encryptionKeys = hdWallet.encryptionKeys(publicKey)
        print(encryptionKeys.associatedPublicKey.toHexString())
        print(encryptionKeys.hmacKey.toHexString())
        print(encryptionKeys.encryptionKey.toHexString())

        assertThat(encryptionKeys.associatedPublicKey.toHexString())
            .isNotEmpty()
        assertThat(encryptionKeys.encryptionKey.toHexString())
            .isNotEmpty()
        assertThat(encryptionKeys.hmacKey.toHexString())
            .isNotEmpty()

        val message = "Howdy Doody"
        val dataToEncrypt = message.toByteArray()

        val encryptedMessage = messageCryptor.encryptAsBase64(
            dataToEncrypt,
            encryptionKeys.encryptionKey,
            encryptionKeys.hmacKey,
            encryptionKeys.associatedPublicKey
        )

        assertThat(String(messageCryptor.decrypt(encryptedMessage, encryptionKeys.encryptionKey, encryptionKeys.hmacKey))).isEqualTo("Howdy Doody")
    }

    @Test
    fun decryption_test() {
        val encryption = "030005f0b1908bb05b53cbc9bbbce0d830e2b6cad7803f35a328ea4c760e6d4228c8c630bba23a9bd84d723ce7c92e1ea82156085033aeeccbd3bcd9fdd99990b1c037b58aeab59a3dccb2fdb063f2d56d95048e300be0c6b0517f63f8b54fbb52212d1903737375416109f386488e17aee465f71bc2755bdd61031f0b1509bf10bc3540204a5de4b3024c9520f63db98708a6".hexToBytes()
        val words = arrayOf(
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "about"
        )

        val hdWallet = HDWallet(words)
        val derivationPath = DerivationPath(49, 0, 0, 0, 0)

        // receiver extracts ephemeralPublicKey from the encrypted payload
        val ephemeralPublicKey = messageCryptor.unpackEphemeralPublicKey(encryption)
        //assertThat(ephemeralPublicKey.toHexString()).isEqualTo("04f50e14dda4ba813f6d50849ce7d2300a68cf40f4380b102b0f9927865323b38d91187aacc04080c0684c8748d97eb3b0fc34cb4bcecd9b9e44e4765264091f20")

        //receiver builds decryption keys from ephemeral public key and derivationPath
        val decryptionKeys = hdWallet.decryptionKeys(derivationPath, ephemeralPublicKey)

        val decryptedMessage = messageCryptor.decrypt(
            encryption,
            decryptionKeys.encryptionKey,
            decryptionKeys.hmacKey
        )

        assertThat(String(decryptedMessage)).isEqualTo("15 bucks, little man")
    }

    @Test
    fun decrypt_payload() {
        //val encryptedMessage = "AwBl/EC+HAGzPay4C9hVjUt8LfYY9whI5UO+HPN58xsWR/8Viil30cY0DjV+soNojKyHk1BNiF2CIg48oMjjZEQC/3gCcqpM8NuBgNuW74+jW3e06/PbWOqTkKsYggsEKKBrWfXAPBWRAP+4/rL1vVVPlRo1rJFRgEOK6kzw6t5HXdi2ivHJ/Dy6gSyisMb/yk6S8dGYtk3R1ajgkxtk30X3B575205olTTB5gcW5BUA8jDtyRByp2rLZnpPguH/lnEMWxS9FvpRojlXFK6ylmVOLgMBaRQhIE0rHOye2vLpc2eXkup5n/jsyftiXxnEphabBD7hW6+EFSn98uVE0bFL5cZVjGcEoP9cfCbwP/UaNwT1DhTdpLqBP21QhJzn0jAKaM9A9DgLECsPmSeGUyOzjZEYeqzAQIDAaEyHSNl+s7D8NMtLzs2bnkTkdlJkCR8g"
        val encryptedMessage =
            "AwAF8LGQi7BbU8vJu7zg2DDitsrXgD81oyjqTHYObUIoyMYwu6I6m9hNcjznyS4eqCFWCFAzruzL07zZ/dmZkLHAN7WK6rWaPcyy/bBj8tVtlQSOMAvgxrBRf2P4tU+7UiEtGQNzc3VBYQnzhkiOF67kZfcbwnVb3WEDHwsVCb8QvDVAIEpd5LMCTJUg9j25hwim"
        val words = arrayOf(
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "abandon", "abandon", "abandon", "abandon",
            "abandon", "about"
        )

        val hdWallet = HDWallet(words)
        val derivationPath = DerivationPath(49, 0, 0, 0, 0)

        // receiver extracts ephemeralPublicKey from the encrypted payload
        val ephemeralPublicKey = messageCryptor.unpackEphemeralPublicKey(encryptedMessage)
        //assertThat(ephemeralPublicKey.toHexString()).isEqualTo("04f50e14dda4ba813f6d50849ce7d2300a68cf40f4380b102b0f9927865323b38d91187aacc04080c0684c8748d97eb3b0fc34cb4bcecd9b9e44e4765264091f20")

        //receiver builds decryption keys from ephemeral public key and derivationPath
        val decryptionKeys = hdWallet.decryptionKeys(derivationPath, ephemeralPublicKey)

        val decryptedMessage = messageCryptor.decrypt(
            encryptedMessage,
            decryptionKeys.encryptionKey,
            decryptionKeys.hmacKey
        )

        assertThat(String(decryptedMessage)).isEqualTo("15 bucks, little man")
    }

    @Test
    fun m42_encryption() {
        val memo = "hey dude"
        val aliceWords = arrayOf(
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "abandon",
            "about"
        )
        val bobWords = arrayOf(
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "wonder",
            "tiny"
        )
        val aliceWallet = HDWallet(aliceWords)
        val bobWallet = HDWallet(bobWords)
        val cryptor = MessageCryptor()

        //guard let bobPubKeyHexData = Data(fromHexEncodedString: bobWalletManager.hexEncodedPublicKey)
        val bobPubKeyHexData = bobWallet.verificationKey
        assertThat(bobPubKeyHexData).isEqualTo("03d8e61de04466b82393897981546403f089b4defab671fc23dd7c189c6eaa3116")
        val uncompressedPublicKey = bobPubKeyHexData.hexToBytes()
        val encryptionKeys = aliceWallet.encryptionKeysForM42(uncompressedPublicKey)
        assertThat(encryptionKeys.encryptionKey.toHexString()).isEqualTo("145eff0a52f053b595a2f1b5a4b058db1b8fe9457498c28ba3935cdc8bbf2ad7")
        assertThat(encryptionKeys.hmacKey.toHexString()).isEqualTo("8d01e9f624dc28cbc845d2574a867df7284af16ddf354a5379618ca6fcda050b")
        assertThat(encryptionKeys.associatedPublicKey.toHexString()).isEqualTo("044458596b5c97e716e82015a72c37b5d3fe0c5dc70a4b83d72e7d2eb65920633ed9c584b482baab0e434f372326dbd26de0a5f6600675f4e5fdb8ea0ac8cfa5a6")

        val encryption = cryptor.encrypt(
            memo.toByteArray(),
            encryptionKeys.encryptionKey,
            encryptionKeys.hmacKey,
            encryptionKeys.associatedPublicKey
        )

        val decryptionKeys = bobWallet.decryptionKeysForM42(encryptionKeys.associatedPublicKey)
        assertThat(decryptionKeys.encryptionKey.toHexString()).isEqualTo("145eff0a52f053b595a2f1b5a4b058db1b8fe9457498c28ba3935cdc8bbf2ad7")
        assertThat(decryptionKeys.hmacKey.toHexString()).isEqualTo("8d01e9f624dc28cbc845d2574a867df7284af16ddf354a5379618ca6fcda050b")

        assertThat(
            String(
                cryptor.decrypt(
                    encryption,
                    decryptionKeys.encryptionKey,
                    decryptionKeys.hmacKey
                )
            )
        ).isEqualTo("hey dude")
    }

    @Test
    fun encryption_and_decryption_of_message() {

        val dataToEncrypt = "Hello World".toByteArray()
        val encryptionKey = Base64.decode(encryptionKeyBase64, Base64.NO_WRAP)
        val encryptHmac = Base64.decode(hmacBase64, Base64.NO_WRAP)

        val ephemeralPublicKey = Base64.decode(ephemeralPublicKeyBase64, Base64.NO_WRAP)
        val encrypted =
            messageCryptor.encrypt(dataToEncrypt, encryptionKey, encryptHmac, ephemeralPublicKey)

        assertThat(ephemeralPublicKey).isEqualTo(encrypted.slice((encrypted.size - 65) until encrypted.size).toByteArray())

        val decrypted = messageCryptor.decrypt(encrypted, encryptionKey, encryptHmac)
        assertThat(dataToEncrypt).isEqualTo(decrypted)
    }

    @Test
    fun encryption_and_decryption_of_message_base64Strings() {

        val dataToEncrypt = "Hello World".toByteArray()
        val encryptionKey = Base64.decode(encryptionKeyBase64, Base64.NO_WRAP)
        val encryptHmac = Base64.decode(hmacBase64, Base64.NO_WRAP)

        val ephemeralPublicKey = Base64.decode(ephemeralPublicKeyBase64, Base64.NO_WRAP)
        val decoded = messageCryptor.encryptAsBase64(
            dataToEncrypt,
            encryptionKey,
            encryptHmac,
            ephemeralPublicKey
        )

        val decrypted = messageCryptor.decrypt(decoded, encryptionKey, encryptHmac)
        assertThat(dataToEncrypt).isEqualTo(decrypted)
    }

    @Test
    fun ephemeral_key_from_encrypted() {
        val unpackEphemeralPublicKey =
            messageCryptor.unpackEphemeralPublicKey(Base64.decode(encrypted, Base64.NO_WRAP))
        assertThat(unpackEphemeralPublicKey).isEqualTo(
            Base64.decode(
                ephemeralPublicKeyBase64,
                Base64.NO_WRAP
            )
        )
    }

    @Test
    fun ephemeral_key_from_encrypted_string() {
        val unpackEphemeralPublicKey = messageCryptor.unpackEphemeralPublicKey(encrypted)
        assertThat(unpackEphemeralPublicKey).isEqualTo(
            Base64.decode(
                ephemeralPublicKeyBase64,
                Base64.NO_WRAP
            )
        )
    }
}
