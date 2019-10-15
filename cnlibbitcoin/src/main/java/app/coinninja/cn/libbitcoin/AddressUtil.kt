package app.coinninja.cn.libbitcoin

import app.coinninja.cn.libbitcoin.enum.AddressType
import app.coinninja.cn.libbitcoin.enum.Network

open class AddressUtil constructor(val purpose: Int = 84, val network: Network = Network.MAINNET) {

    init {
        System.loadLibrary("cnlibbitcoin")
    }

    open fun typeOfPaymentAddress(address: String): AddressType =
        if (address.isNotEmpty()) AddressType.from(typeOfAddress(purpose, network.which, address)) else AddressType.UNKNOWN

    open external fun isBase58(address: String): Boolean
    open external fun isSegwit(address: String): Boolean

    private external fun typeOfAddress(purpose: Int, network: Int, address: String): Int

}