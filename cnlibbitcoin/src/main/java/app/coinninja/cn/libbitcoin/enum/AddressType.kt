package app.coinninja.cn.libbitcoin.enum


enum class AddressType constructor(val id: Int) {
    UNKNOWN(-1),
    P2PKH(0),
    P2SH(1),
    P2WPKH(2),
    P2WSH(3);

    companion object {
        fun from(id: Int?): AddressType = when (id) {
            0 -> P2PKH
            1 -> P2SH
            2 -> P2WPKH
            3 -> P2WSH
            else -> UNKNOWN
        }
    }
}
