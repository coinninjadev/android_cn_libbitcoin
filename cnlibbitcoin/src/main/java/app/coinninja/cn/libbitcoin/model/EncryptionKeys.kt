package app.coinninja.cn.libbitcoin.model

import android.os.Parcel
import android.os.Parcelable

open class EncryptionKeys(
    val encryptionKey: ByteArray,
    val hmacKey: ByteArray,
    val associatedPublicKey: ByteArray
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.createByteArray() ?: "".toByteArray(),
        parcel.createByteArray() ?: "".toByteArray(),
        parcel.createByteArray() ?: "".toByteArray()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptionKeys

        if (!encryptionKey.contentEquals(other.encryptionKey)) return false
        if (!hmacKey.contentEquals(other.hmacKey)) return false
        if (!associatedPublicKey.contentEquals(other.associatedPublicKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptionKey.contentHashCode()
        result = 31 * result + hmacKey.contentHashCode()
        result = 31 * result + associatedPublicKey.contentHashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(encryptionKey)
        parcel.writeByteArray(hmacKey)
        parcel.writeByteArray(associatedPublicKey)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EncryptionKeys> {
        override fun createFromParcel(parcel: Parcel): EncryptionKeys {
            return EncryptionKeys(parcel)
        }

        override fun newArray(size: Int): Array<EncryptionKeys?> {
            return arrayOfNulls(size)
        }
    }
}