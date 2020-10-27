package com.app.graffiti.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [CreateOrder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/4/18
 */

class CreateOrder(
        @SerializedName("creatorId")
        val creatorId: Int,
        @SerializedName("userId")
        val userId: Int,
        @SerializedName("baseAmount")
        val baseAmount: Double,
        @SerializedName("discount")
        val discount: Double,
        @SerializedName("tax")
        val tax: Double,
        @SerializedName("totalAmount")
        val totalAmount: Double,
        @SerializedName("productOrder")
        val productOrder: List<ProductOrderData>
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.readDouble(),
            source.createTypedArrayList(ProductOrderData.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(creatorId)
        writeInt(userId)
        writeDouble(baseAmount)
        writeDouble(discount)
        writeDouble(tax)
        writeDouble(totalAmount)
        writeTypedList(productOrder)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CreateOrder> = object : Parcelable.Creator<CreateOrder> {
            override fun createFromParcel(source: Parcel): CreateOrder = CreateOrder(source)
            override fun newArray(size: Int): Array<CreateOrder?> = arrayOfNulls(size)
        }
    }
}