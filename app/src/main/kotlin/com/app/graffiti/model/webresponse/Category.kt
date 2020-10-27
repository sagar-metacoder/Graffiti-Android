package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [Category] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 10/4/18
 */

class Category(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("category")
        val category: String?,
        @SerializedName("image_path")
        val imagePath: String?,
        @SerializedName("subcategory")
        val subCategory: ArrayList<Category>?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.createTypedArrayList(CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(category)
        writeString(imagePath)
        writeTypedList(subCategory)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Category> = object : Parcelable.Creator<Category> {
            override fun createFromParcel(source: Parcel): Category = Category(source)
            override fun newArray(size: Int): Array<Category?> = arrayOfNulls(size)
        }
    }
}