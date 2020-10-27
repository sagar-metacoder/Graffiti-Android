package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [TargetScheme] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 25/6/18
 */

class TargetScheme(
        @SerializedName("id")
        private val id: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("start_date")
        val startDate: String?,
        @SerializedName("end_date")
        val endDate: String?,
        @SerializedName("image")
        val image: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(title)
        writeString(description)
        writeString(startDate)
        writeString(endDate)
        writeString(image)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TargetScheme> = object : Parcelable.Creator<TargetScheme> {
            override fun createFromParcel(source: Parcel): TargetScheme = TargetScheme(source)
            override fun newArray(size: Int): Array<TargetScheme?> = arrayOfNulls(size)
        }
    }
}
