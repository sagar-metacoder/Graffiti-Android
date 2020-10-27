package com.app.graffiti.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * [ProductOrderData] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/4/18
 */

class ProductOrderData(
        var productName: String,
        @SerializedName("productId")
        var productId: Int,
        @SerializedName("quantity")
        var quantity: Int,
        @SerializedName("price")
        var price: Double,
        @SerializedName("total")
        var total: Double
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readDouble(),
            source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(productName)
        writeInt(productId)
        writeInt(quantity)
        writeDouble(price)
        writeDouble(total)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ProductOrderData> = object : Parcelable.Creator<ProductOrderData> {
            override fun createFromParcel(source: Parcel): ProductOrderData = ProductOrderData(source)
            override fun newArray(size: Int): Array<ProductOrderData?> = arrayOfNulls(size)
        }
    }
}