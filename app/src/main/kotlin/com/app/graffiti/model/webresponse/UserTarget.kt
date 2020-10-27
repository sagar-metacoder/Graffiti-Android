package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [UserTarget] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 5/4/18
 */

class UserTarget(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("type")
        val type: Int?,
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("target")
        val target: String?,
        @SerializedName("achived")
        val achieved: String?,
        @SerializedName("start_date")
        val startDate: String?,
        @SerializedName("end_date")
        val endDate: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("date")
        val date: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
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
        writeValue(type)
        writeValue(userId)
        writeString(title)
        writeString(target)
        writeString(achieved)
        writeString(startDate)
        writeString(endDate)
        writeString(status)
        writeString(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserTarget> = object : Parcelable.Creator<UserTarget> {
            override fun createFromParcel(source: Parcel): UserTarget = UserTarget(source)
            override fun newArray(size: Int): Array<UserTarget?> = arrayOfNulls(size)
        }
    }
}
