package app.coinninja.cn.libbitcoin

import app.coinninja.cn.libbitcoin.enum.AddressType
import com.google.common.truth.Truth.assertThat
import org.junit.Ignore
import org.junit.Test


class AddressUtilTest {

    // Base58 encoding

    @Test
    fun does_base58_check__test_net() {
        assertThat(AddressUtil().isBase58("2Mww8dCYPUpKHofjgcXcBCEGmniw9CoaiD2")).isTrue()
    }

    @Test
    fun does_base58_check__p2sh() {
        assertThat(AddressUtil().isBase58("394YWe6P48qdfFEc25wp2m8tuPF1eJFqBe")).isTrue()
    }

    @Test
    fun does_base58_check__p2sh__bad() {
        assertThat(AddressUtil().isBase58("394YWe6P48qdfFEc25wp2m8tuPF1eJFqBejjj")).isFalse()
    }

    @Test
    fun does_base58_check__p2pkh() {
        assertThat(AddressUtil().isBase58("185GYRXheZckJNBHGEdt4b9GSYcXV9r7fw")).isTrue()
    }

    @Test
    fun does_base58_check__p2pkh__bad() {
        assertThat(AddressUtil().isBase58("185GYRXheZckJNBHGEdt4b9GSYcXV9r7fw555")).isFalse()
    }

    @Test
    fun bitcoin_uri() {
        assertThat(AddressUtil().isBase58("bitcoin:394YWe6P48qdfFEc25wp2m8tuPF1eJFqBe")).isFalse()
    }

    @Test
    fun empty_string() {
        assertThat(AddressUtil().isBase58("")).isFalse()
    }

    // Segwit Encoding

    @Test
    fun test_net_segwit_address_verification() {
        assertThat(AddressUtil().isSegwit("tb1q9u62588spffmq4dzjxsr5l297znf3z6j5p2688")).isTrue()
    }

    @Test
    fun main_net_address_verification() {
        assertThat(AddressUtil().isSegwit("bc1q8c6fshw2dlwun7ekn9qwf37cu2rn755upcp6el")).isTrue()
    }

    @Test
    fun rubbish() {
        assertThat(AddressUtil().isSegwit("foo")).isFalse()
        assertThat(AddressUtil().isBase58("foo")).isFalse()
    }

    // Address Type

    @Test
    fun returns_version_for_given_address__UNKONWN() {
        val addressUtil = AddressUtil()
        assertThat(addressUtil.typeOfPaymentAddress("")).isEqualTo(AddressType.UNKNOWN)
    }

    @Test
    fun returns_version_for_given_address__P2SH() {
        val address = "3EktnHQD7RiAE6uzMj2ZifT9YgRrkSgzQX"
        val addressUtil = AddressUtil()
        assertThat(addressUtil.typeOfPaymentAddress(address)).isEqualTo(AddressType.P2SH)
        assertThat(addressUtil.typeOfPaymentAddress(address)).isEqualTo(AddressType.P2SH)
        assertThat(addressUtil.typeOfPaymentAddress(address)).isEqualTo(AddressType.P2SH)
        assertThat(addressUtil.typeOfPaymentAddress(address)).isEqualTo(AddressType.P2SH)
        assertThat(addressUtil.typeOfPaymentAddress(address)).isEqualTo(AddressType.P2SH)
    }

    @Test
    fun returns_version_for_given_address__P2PKH() {
        val address = "17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem"
        val addressUtil = AddressUtil()
        assertThat(addressUtil.typeOfPaymentAddress(address)).isEqualTo(AddressType.P2PKH)
    }

    @Test
    fun returns_version_for_given_address__P2WPKH() {
        val address = "bc1q8c6fshw2dlwun7ekn9qwf37cu2rn755upcp6el"
        val addressUtil = AddressUtil()
        assertThat(addressUtil.typeOfPaymentAddress(address)).isEqualTo(AddressType.P2WPKH)
    }


}