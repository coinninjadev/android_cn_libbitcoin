import app.coinninja.cn.libbitcoin.EntropyGenerator
import app.coinninja.cn.libbitcoin.HDWallet
import app.coinninja.cn.libbitcoin.SeedWordGenerator
import org.junit.Test

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Ignore
import java.security.SecureRandom

class SeedWordGeneratorTest {

    @Test
    fun generates_12_words() {
        assertThat(SeedWordGenerator().generate().size).isEqualTo(12)
    }

    @Test
    fun returns_empty_list_when_checksum_not_valid() {
        assertThat(HDWallet().newWords(SecureRandom.getSeed(15))).isEmpty()
    }

    @Test
    fun retry_word_generation_when_() {
        val entropyGenerator:EntropyGenerator = mock()
        whenever(entropyGenerator.generateEntropy(16))
            .thenReturn(SecureRandom.getSeed(15))
            .thenReturn(SecureRandom.getSeed(16))

        val wallet = HDWallet(entropyGenerator = entropyGenerator)

        assertThat(wallet.newWordsWithChecksumRetry().size).isEqualTo(12)
    }
}