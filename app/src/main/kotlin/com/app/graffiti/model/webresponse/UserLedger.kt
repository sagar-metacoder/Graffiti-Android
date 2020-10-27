package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [UserLedger] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/6/18
 */

class UserLedger(
        @SerializedName("page")
        val page: String?,
        @SerializedName("total_amount")
        val totalAmount: Double?,
        @SerializedName("amount")
        val amount: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("pending_amount")
        val pendingAmount: Double?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Double::class.java.classLoader) as Double?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(page)
        writeValue(totalAmount)
        writeString(amount)
        writeString(date)
        writeString(description)
        writeValue(pendingAmount)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserLedger> = object : Parcelable.Creator<UserLedger> {
            override fun createFromParcel(source: Parcel): UserLedger = UserLedger(source)
            override fun newArray(size: Int): Array<UserLedger?> = arrayOfNulls(size)
        }
    }
}