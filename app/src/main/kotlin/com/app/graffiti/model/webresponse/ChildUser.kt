package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [ChildUser] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 5/4/18
 */

class ChildUser(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("firm_name")
        val firmName: String?,
        @SerializedName("gst_no")
        val gstNumber: String?,
        @SerializedName("address")
        val address: String?,
        @SerializedName("mobile_no")
        val mobileNumber: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("image_path")
        val imagePath: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("distance")
        val distance: Int?,
        @SerializedName("date")
        val date: String?
) : Parcelable {
    constructor(source: Parcel) : this(
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
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(firstName)
        writeString(lastName)
        writeString(firmName)
        writeString(gstNumber)
        writeString(address)
        writeString(mobileNumber)
        writeString(email)
        writeString(imagePath)
        writeString(status)
        writeValue(distance)
        writeString(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ChildUser> = object : Parcelable.Creator<ChildUser> {
            override fun createFromParcel(source: Parcel): ChildUser = ChildUser(source)
            override fun newArray(size: Int): Array<ChildUser?> = arrayOfNulls(size)
        }
    }
}