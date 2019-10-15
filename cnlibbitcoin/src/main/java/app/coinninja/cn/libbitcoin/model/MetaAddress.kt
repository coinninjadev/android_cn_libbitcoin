package app.coinninja.cn.libbitcoin.model

import android.os.Parcel
import android.os.Parcelable

open class MetaAddress @JvmOverloads constructor(
    var address: String,
    var pubKey: String = "",
    var derivationPath: DerivationPath? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(DerivationPath::class.java.classLoader)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetaAddress

        if (address != other.address) return false
        if (pubKey != other.pubKey) return false
        if (derivationPath != other.derivationPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + pubKey.hashCode()
        result = 31 * result + derivationPath.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(pubKey)
        parcel.writeParcelable(derivationPath, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MetaAddress> {
        override fun createFromParcel(parcel: Parcel): MetaAddress {
            return MetaAddress(parcel)
        }

        override fun newArray(size: Int): Array<MetaAddress?> {
            return arrayOfNulls(size)
        }
    }
}

