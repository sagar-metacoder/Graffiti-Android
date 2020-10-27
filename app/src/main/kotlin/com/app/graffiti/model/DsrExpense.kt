package com.app.graffiti.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [DsrExpense] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/5/18
 */

class DsrExpense(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("creator_id")
        val creatorId: Int?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("from_address")
        val fromAddress: String?,
        @SerializedName("to_address")
        val toAddress: String?,
        @SerializedName("mode")
        val mode: String?,
        @SerializedName("fare")
        val fare: String?,
        @SerializedName("logging")
        val logging: String?,
        @SerializedName("boarding")
        val boarding: String?,
        @SerializedName("extra")
        val extra: String?,
        @SerializedName("miscellaneous")
        val miscellaneous: String?,
        @SerializedName("total")
        val total: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeValue(creatorId)
        writeString(date)
        writeString(fromAddress)
        writeString(toAddress)
        writeString(mode)
        writeString(fare)
        writeString(logging)
        writeString(boarding)
        writeString(extra)
        writeString(miscellaneous)
        writeString(total)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DsrExpense> = object : Parcelable.Creator<DsrExpense> {
            override fun createFromParcel(source: Parcel): DsrExpense = DsrExpense(source)
            override fun newArray(size: Int): Array<DsrExpense?> = arrayOfNulls(size)
        }
    }
}
