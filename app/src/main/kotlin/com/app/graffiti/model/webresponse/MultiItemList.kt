package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [MultiItemList] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 16/4/18
 */

class MultiItemList(
        @SerializedName("order_id")
        val orderId: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("item")
        val itemData: ItemData?,
        @SerializedName("isDeleteable")
        val isDeleteable: Boolean?
) : Parcelable {
    data class ItemData(
            @SerializedName("user_id")
            val userId: String?,
            @SerializedName("product_id")
            val productId: Int?,
            @SerializedName("quantity")
            val quantity: Int?,
            @SerializedName("product_name")
            val productName: String?,
            @SerializedName("item_code")
            val itemCode: String?,
            @SerializedName("product_image")
            val productImage: String?
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString(),
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readString(),
                source.readString(),
                source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(userId)
            writeValue(productId)
            writeValue(quantity)
            writeString(productName)
            writeString(itemCode)
            writeString(productImage)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<ItemData> = object : Parcelable.Creator<ItemData> {
                override fun createFromParcel(source: Parcel): ItemData = ItemData(source)
                override fun newArray(size: Int): Array<ItemData?> = arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readParcelable<ItemData>(ItemData::class.java.classLoader),
            if (source.readInt() == 1) true else false
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(orderId)
        writeString(title)
        writeString(type)
        writeParcelable(itemData, 0)
        if (isDeleteable == true) writeInt(1) else writeInt(0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MultiItemList> = object : Parcelable.Creator<MultiItemList> {
            override fun createFromParcel(source: Parcel): MultiItemList = MultiItemList(source)
            override fun newArray(size: Int): Array<MultiItemList?> = arrayOfNulls(size)
        }
    }
}
