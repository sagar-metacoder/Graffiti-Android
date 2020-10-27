package com.app.graffiti.model.webresponse
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class MyTeamResponse(
        @SerializedName("id")
        val id: String,
        @SerializedName("user_type")
        val userType: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("last_name")
        val lastName: String,
        @SerializedName("firm_name")
        val firmName: String,
        @SerializedName("mobile_no")
        val mobileNo: String
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(userType)
        writeString(firstName)
        writeString(lastName)
        writeString(firmName)
        writeString(mobileNo)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MyTeamResponse> = object : Parcelable.Creator<MyTeamResponse> {
            override fun createFromParcel(source: Parcel): MyTeamResponse = MyTeamResponse(source)
            override fun newArray(size: Int): Array<MyTeamResponse?> = arrayOfNulls(size)
        }
    }
}