package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [NotificationData] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 20/6/18
 */

class NotificationData(
        @SerializedName("id")
        val id: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("date")
        val date: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(title)
        writeString(description)
        writeString(image)
        writeString(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<NotificationData> = object : Parcelable.Creator<NotificationData> {
            override fun createFromParcel(source: Parcel): NotificationData = NotificationData(source)
            override fun newArray(size: Int): Array<NotificationData?> = arrayOfNulls(size)
        }
    }
}