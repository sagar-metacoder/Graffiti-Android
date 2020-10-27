package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [UserDsrData] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 13/4/18
 */

class UserDsrData(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("address")
        val address: String?,
        @SerializedName("mobile_no")
        val mobileNumber: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("user_type")
        val userType: Int?,
        @SerializedName("firm_name")
        val firmName: String?
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
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(description)
        writeString(date)
        writeString(firstName)
        writeString(lastName)
        writeString(address)
        writeString(mobileNumber)
        writeString(email)
        writeString(status)
        writeValue(userType)
        writeString(firmName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserDsrData> = object : Parcelable.Creator<UserDsrData> {
            override fun createFromParcel(source: Parcel): UserDsrData = UserDsrData(source)
            override fun newArray(size: Int): Array<UserDsrData?> = arrayOfNulls(size)
        }
    }
}
