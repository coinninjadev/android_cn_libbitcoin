package app.coinninja.cn.libbitcoin.util

import java.util.*

fun ByteArray.toHexString(): String {

    val formatter = Formatter()

    forEach { byte ->
        formatter.format("%02x", byte)
    }

    return formatter.toString()
}
