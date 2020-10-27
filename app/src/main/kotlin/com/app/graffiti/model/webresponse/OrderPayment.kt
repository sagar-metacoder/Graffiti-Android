package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [OrderPayment] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 2/5/18
 */

class OrderPayment(
        @SerializedName("id")
        val id: String?,
        @SerializedName("user_id")
        val userId: String?,
        @SerializedName("amount")
        val amount: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("insert_date")
        val insertDate: String?
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
        writeString(userId)
        writeString(amount)
        writeString(date)
        writeString(description)
        writeString(insertDate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderPayment> = object : Parcelable.Creator<OrderPayment> {
            override fun createFromParcel(source: Parcel): OrderPayment = OrderPayment(source)
            override fun newArray(size: Int): Array<OrderPayment?> = arrayOfNulls(size)
        }
    }
}

/*class OrderPayment(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("creator_id")
        val creatorId: String?,
        @SerializedName("user_id")
        val userId: String?,
        @SerializedName("base_amount")
        val baseAmount: Double,
        @SerializedName("discount")
        val discount: String?,
        @SerializedName("tax")
        val tax: String?,
        @SerializedName("total_amount")
        val totalAmount: Double,
        @SerializedName("insert_order_date")
        val insertedOrderDate: String?,
        @SerializedName("insert_complete_date")
        val insertedCompleteDate: String?,
//        @SerializedName("creator")
//        val creator: OrderItem.User?,
//        @SerializedName("user")
//        val user: OrderItem.User?,
        @SerializedName("payment")
        val paymentList: ArrayList<Payment>?
) : Parcelable {
    class Payment(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("order_id")
            val orderId: Int?,
            @SerializedName("amount")
            val amount: Double,
            @SerializedName("date")
            val date: String?,
            @SerializedName("description")
            val description: String?,
            @SerializedName("insert_date")
            val insertDate: String?
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readDouble(),
                source.readString(),
                source.readString(),
                source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeValue(id)
            writeValue(orderId)
            writeDouble(amount)
            writeString(date)
            writeString(description)
            writeString(insertDate)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Payment> = object : Parcelable.Creator<Payment> {
                override fun createFromParcel(source: Parcel): Payment = Payment(source)
                override fun newArray(size: Int): Array<Payment?> = arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readDouble(),
            source.readString(),
            source.readString(),
            source.readDouble(),
            source.readString(),
            source.readString(),
//            source.readParcelable<OrderItem.User>(OrderItem.User::class.java.classLoader),
//            source.readParcelable<OrderItem.User>(OrderItem.User::class.java.classLoader),
            source.createTypedArrayList(Payment.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(creatorId)
        writeString(userId)
        writeDouble(baseAmount)
        writeString(discount)
        writeString(tax)
        writeDouble(totalAmount)
        writeString(insertedOrderDate)
        writeString(insertedCompleteDate)
//        writeParcelable(creator, 0)
//        writeParcelable(user, 0)
        writeTypedList(paymentList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderPayment> = object : Parcelable.Creator<OrderPayment> {
            override fun createFromParcel(source: Parcel): OrderPayment = OrderPayment(source)
            override fun newArray(size: Int): Array<OrderPayment?> = arrayOfNulls(size)
        }
    }
}*/
