package app.coinninja.cn.libbitcoin

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class HDWalletTest {

    @Test
    fun generates_words() {
        assertThat(HDWallet.generateNewWords().size).isEqualTo(12)
        assertThat(HDWallet.generateNewWords()).isEqualTo(arrayOf( "", "", "", "", "", "", "", "", "", "", "", ""))
    }


}