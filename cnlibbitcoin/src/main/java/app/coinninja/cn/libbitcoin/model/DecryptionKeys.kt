package app.coinninja.cn.libbitcoin.model

import android.os.Parcel
import android.os.Parcelable

open class DecryptionKeys(
    val encryptionKey: ByteArray,
    val hmacKey: ByteArray
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createByteArray() ?: "".toByteArray(),
        parcel.createByteArray() ?: "".toByteArray()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecryptionKeys

        if (!encryptionKey.contentEquals(other.encryptionKey)) return false
        if (!hmacKey.contentEquals(other.hmacKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptionKey.contentHashCode()
        result = 31 * result + hmacKey.contentHashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(encryptionKey)
        parcel.writeByteArray(hmacKey)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DecryptionKeys> {
        override fun createFromParcel(parcel: Parcel): DecryptionKeys {
            return DecryptionKeys(parcel)
        }

        override fun newArray(size: Int): Array<DecryptionKeys?> {
            return arrayOfNulls(size)
        }
    }
}