package app.coinninja.cn.libbitcoin.model

import android.os.Parcel
import android.os.Parcelable

open class Transaction(
    val txid: String,
    val encodedTransaction: String
) : Parcelable {

    constructor() :this("", "")

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(txid)
        parcel.writeString(encodedTransaction)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (txid != other.txid) return false
        if (encodedTransaction != other.encodedTransaction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = txid.hashCode()
        result = 31 * result + encodedTransaction.hashCode()
        return result
    }


    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}
