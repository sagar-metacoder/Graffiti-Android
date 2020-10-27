package com.app.graffiti.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [SendExpense] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 12/4/18
 */

class SendExpense(
        @SerializedName("creator_id")
        val id: Int,
        @SerializedName("date")
        val date: String,
        @SerializedName("other_comment")
        val otherComment: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(date)
        writeString(otherComment)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SendExpense> = object : Parcelable.Creator<SendExpense> {
            override fun createFromParcel(source: Parcel): SendExpense = SendExpense(source)
            override fun newArray(size: Int): Array<SendExpense?> = arrayOfNulls(size)
        }
    }
}