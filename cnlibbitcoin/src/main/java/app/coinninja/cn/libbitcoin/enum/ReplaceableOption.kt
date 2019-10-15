package app.coinninja.cn.libbitcoin.enum


enum class ReplaceableOption constructor(val id: Int) {
    MUST_BE_RBF(0),
    MUST_NOT_BE_RBF(1),
    RBF_ALLOWED(2);

    companion object {
        fun from(id: Int): ReplaceableOption = when (id) {
            0 -> MUST_BE_RBF
            1 -> MUST_NOT_BE_RBF
            2 -> RBF_ALLOWED
            else -> RBF_ALLOWED
        }
    }
}
