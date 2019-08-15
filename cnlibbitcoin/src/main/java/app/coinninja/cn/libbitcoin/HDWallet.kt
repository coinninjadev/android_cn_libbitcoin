package app.coinninja.cn.libbitcoin

import java.security.SecureRandom

class HDWallet {

    constructor() {
        System.loadLibrary("cnlibbitcoin")
    }

    private external fun newWords(entropy: ByteArray): Array<String>

    companion object {

        fun generateNewWords(): Array<String> {
            return HDWallet().newWords(SecureRandom.getSeed(16))
        }

    }

}
