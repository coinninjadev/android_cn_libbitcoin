package app.coinninja.cn.libbitcoin.model

import android.os.Parcel
import android.os.Parcelable

open class DerivationPath(
    val purpose: Int,
    val coin: Int,
    val account: Int,
    val chain: Int,
    val index: Int
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DerivationPath

        if (purpose != other.purpose) return false
        if (coin != other.coin) return false
        if (account != other.account) return false
        if (chain != other.chain) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = purpose
        result = 31 * result + coin
        result = 31 * result + account
        result = 31 * result + chain
        result = 31 * result + index
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(purpose)
        parcel.writeInt(coin)
        parcel.writeInt(account)
        parcel.writeInt(chain)
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    open override fun toString(): String = "M/${purpose}/${coin}/${account}/${chain}/${index}"

    open fun toInts(): IntArray = intArrayOf(purpose, coin, account, chain, index)

    companion object CREATOR : Parcelable.Creator<DerivationPath> {
        override fun createFromParcel(parcel: Parcel): DerivationPath {
            return DerivationPath(parcel)
        }

        override fun newArray(size: Int): Array<DerivationPath?> {
            return arrayOfNulls(size)
        }

        fun from(path: String): DerivationPath {
            val parts = path.replace("M/", "").trim().split("/")
            if (parts.size <= 4) throw IllegalArgumentException("missing parts")
            return DerivationPath(
                purpose = Integer.parseInt(parts[0]),
                coin = Integer.parseInt(parts[1]),
                account = Integer.parseInt(parts[2]),
                chain = Integer.parseInt(parts[3]),
                index = Integer.parseInt(parts[4])
            )
        }
    }
}
