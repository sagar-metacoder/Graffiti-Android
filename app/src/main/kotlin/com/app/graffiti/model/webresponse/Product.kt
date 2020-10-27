package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [Product] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 10/4/18
 */

class Product(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("product_name")
        val productName: String?,
        @SerializedName("item_id")
        val itemId: String?,
        @SerializedName("item_code")
        val itemCode: String?,
        @SerializedName("items")
        val items: String?,
        @SerializedName("main_category_id")
        val mainCategoryId: Int?,
        @SerializedName("sub_category_id")
        val subCategoryId: Int?,
        @SerializedName("stock_category")
        val stockCategory: String?,
        @SerializedName("collection")
        val collection: String?,
        @SerializedName("mrp")
        val mrp: Double?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("image_path")
        val imagePath: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("date")
        val date: String?,
        val shouldShowCart: Boolean
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            if (source.readInt() == 1) true else false
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(productName)
        writeString(itemId)
        writeString(itemCode)
        writeString(items)
        writeValue(mainCategoryId)
        writeValue(subCategoryId)
        writeString(stockCategory)
        writeString(collection)
        writeValue(mrp)
        writeString(description)
        writeString(image)
        writeString(imagePath)
        writeString(status)
        writeString(date)
        if (shouldShowCart) writeInt(1) else writeInt(0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Product> = object : Parcelable.Creator<Product> {
            override fun createFromParcel(source: Parcel): Product = Product(source)
            override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
        }
    }
}















