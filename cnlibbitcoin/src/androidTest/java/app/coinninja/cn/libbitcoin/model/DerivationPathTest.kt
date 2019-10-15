package app.coinninja.cn.libbitcoin.model

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test


class DerivationPathTest {

    @Test
    fun path_to_ints() {
        assertThat(DerivationPath(49, 1, 0, 1, 50).toInts()).isEqualTo(intArrayOf(49, 1, 0, 1, 50))
    }

    @Test
    fun to_string_returns_hardened_path() {
        assertThat(DerivationPath(49, 1, 0, 1, 50).toString()).isEqualTo("M/49/1/0/1/50")
    }

    @Test
    fun given_hardened_path_converts_inits_derivation_path() {
        val derivationPath = DerivationPath.from("M/49/1/0/1/50")
        assertThat(derivationPath.purpose).isEqualTo(49)
        assertThat(derivationPath.coin).isEqualTo(1)
        assertThat(derivationPath.account).isEqualTo(0)
        assertThat(derivationPath.chain).isEqualTo(1)
        assertThat(derivationPath.index).isEqualTo(50)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throws_if_invalid_path_init_from_string() {
        DerivationPath.from("M/49/1/1/50")
    }
}