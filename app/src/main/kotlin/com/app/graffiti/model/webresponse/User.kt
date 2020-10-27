package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [User] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 5/4/18
 */

class User(
        @SerializedName("id")
        val userId: Int?,
        @SerializedName("user_type")
        val userType: Int?,
        @SerializedName("login_session")
        val logInSession: String?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("last_name")
        val lastName: String?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(userId)
        writeValue(userType)
        writeString(logInSession)
        writeString(firstName)
        writeString(lastName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}