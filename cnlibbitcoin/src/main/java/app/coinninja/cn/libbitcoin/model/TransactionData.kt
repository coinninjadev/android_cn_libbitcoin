package app.coinninja.cn.libbitcoin.model

import android.os.Parcel
import android.os.Parcelable
import app.coinninja.cn.libbitcoin.enum.ReplaceableOption
import app.coinninja.cn.libbitcoin.enum.ReplaceableOption.RBF_ALLOWED

open class TransactionData @JvmOverloads constructor(
    var utxos: Array<UnspentTransactionOutput> = emptyArray(),
    var amount: Long = 0,
    var feeAmount: Long = 0,
    var changeAmount: Long = 0,
    var changePath: DerivationPath? = null,
    var paymentAddress: String? = null,
    var replaceableOption: ReplaceableOption = RBF_ALLOWED,
    var blockHeight: Long = 0
) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        parcel.createTypedArray(UnspentTransactionOutput.CREATOR) as Array<UnspentTransactionOutput>,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readParcelable(DerivationPath::class.java.classLoader),
        parcel.readString(),
        ReplaceableOption.from(parcel.readInt())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedArray(utxos, flags)
        parcel.writeLong(amount)
        parcel.writeLong(feeAmount)
        parcel.writeLong(changeAmount)
        parcel.writeParcelable(changePath, flags)
        parcel.writeString(paymentAddress)
        parcel.writeInt(replaceableOption.id)
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionData

        if (!utxos.contentEquals(other.utxos)) return false
        if (amount != other.amount) return false
        if (feeAmount != other.feeAmount) return false
        if (changeAmount != other.changeAmount) return false
        if (changePath != other.changePath) return false
        if (paymentAddress != other.paymentAddress) return false
        if (replaceableOption != other.replaceableOption) return false

        return true
    }

    override fun hashCode(): Int {
        var result = utxos.contentHashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + feeAmount.hashCode()
        result = 31 * result + changeAmount.hashCode()
        result = 31 * result + (changePath?.hashCode() ?: 0)
        result = 31 * result + (paymentAddress?.hashCode() ?: 0)
        result = 31 * result + replaceableOption.id.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<TransactionData> {
        override fun createFromParcel(parcel: Parcel): TransactionData {
            return TransactionData(parcel)
        }

        override fun newArray(size: Int): Array<TransactionData?> {
            return arrayOfNulls(size)
        }
    }


}
