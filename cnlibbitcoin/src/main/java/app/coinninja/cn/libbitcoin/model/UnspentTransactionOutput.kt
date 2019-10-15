package app.coinninja.cn.libbitcoin.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

open class UnspentTransactionOutput @JvmOverloads constructor(
    var txid: String = "",
    var index: Int = 0,
    var amount: Long = 0,
    var path: DerivationPath? = null,
    var replaceable: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readLong(),
        parcel.readParcelable(DerivationPath::class.java.classLoader),
        parcel.readInt() == 1
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(txid)
        parcel.writeInt(index)
        parcel.writeLong(amount)
        parcel.writeParcelable(path, flags)
        parcel.writeInt(if (replaceable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnspentTransactionOutput

        if (txid != other.txid) return false
        if (index != other.index) return false
        if (amount != other.amount) return false
        if (path != other.path) return false
        if (replaceable != other.replaceable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = txid.hashCode()
        result = 31 * result + index
        result = 31 * result + amount.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + replaceable.hashCode()
        return result
    }

    companion object CREATOR : Creator<UnspentTransactionOutput> {
        override fun createFromParcel(parcel: Parcel): UnspentTransactionOutput {
            return UnspentTransactionOutput(parcel)
        }

        override fun newArray(size: Int): Array<UnspentTransactionOutput?> {
            return arrayOfNulls(size)
        }
    }
}