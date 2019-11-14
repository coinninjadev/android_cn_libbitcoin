package app.coinninja.cn.libbitcoin

import java.security.SecureRandom

open class EntropyGenerator {

    open fun generateEntropy(numBytes:Int):ByteArray {
        return SecureRandom().generateSeed(numBytes)
    }

}