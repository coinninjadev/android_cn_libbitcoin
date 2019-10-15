package app.coinninja.cn.libbitcoin

open class SeedWordGenerator {
    open fun generate(): Array<String> = HDWallet.generateNewWords()
}