package com.app.graffiti.model

import android.os.Parcel
import android.os.Parcelable
import com.app.graffiti.model.webresponse.*
import com.google.gson.annotations.SerializedName

/**
 * [GeneralResponse] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 5/4/18
 */

public class GeneralResponse(
        @SerializedName("status")
        val status: Int?,
        @SerializedName("message")
        val message: Message?,
        @SerializedName("data")
        val data: Data?
) : Parcelable {
    class Message(
            @SerializedName("success")
            val success: String?,
            @SerializedName("error")
            val error: String?
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString(),
                source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(success)
            writeString(error)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Message> = object : Parcelable.Creator<Message> {
                override fun createFromParcel(source: Parcel): Message = Message(source)
                override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
            }
        }
    }

    class Data(
            @SerializedName("user")
            val user: User?,
            @SerializedName("target")
            val userTarget: ArrayList<UserTarget>?,
            @SerializedName("distributor")
            val distributorList: ArrayList<ChildUser>?,
            @SerializedName("dealer")
            val dealerList: ArrayList<ChildUser>?,
            @SerializedName("category")
            val categoryList: ArrayList<Category>?,
            @SerializedName("Product")
            val productList: ArrayList<Product>?,
            @SerializedName("dsr_temp_user_list")
            val dsrTempUserData: ArrayList<UserDsrData>?,
            @SerializedName("dsr_user_list")
            val dsrUserData: ArrayList<UserDsrData>?,
            @SerializedName("cart_list")
            private val cartList: ArrayList<MultiItemList>?,
            @SerializedName("order_list")
            val orderList: ArrayList<OrderItem>?,
            @SerializedName("orderPayment")
            val orderPaymentList: ArrayList<OrderPayment>?,
            @SerializedName("sales_expenses")
            val salesExpenseList: ArrayList<DsrExpense>?,
            @SerializedName("notification")
            val notificationList: ArrayList<NotificationData>?,
            @SerializedName("scheme")
            val targetSchemeList: ArrayList<TargetScheme>?,
            @SerializedName("legder")
            val userLedgerList: ArrayList<UserLedger>?,
            @SerializedName("member")
            val myTeamList: ArrayList<MyTeamResponse>?

    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readParcelable<User>(User::class.java.classLoader),
                source.createTypedArrayList(UserTarget.CREATOR),
                source.createTypedArrayList(ChildUser.CREATOR),
                source.createTypedArrayList(ChildUser.CREATOR),
                source.createTypedArrayList(Category.CREATOR),
                source.createTypedArrayList(Product.CREATOR),
                source.createTypedArrayList(UserDsrData.CREATOR),
                source.createTypedArrayList(UserDsrData.CREATOR),
                source.createTypedArrayList(MultiItemList.CREATOR),
                source.createTypedArrayList(OrderItem.CREATOR),
                source.createTypedArrayList(OrderPayment.CREATOR),
                source.createTypedArrayList(DsrExpense.CREATOR),
                source.createTypedArrayList(NotificationData.CREATOR),
                source.createTypedArrayList(TargetScheme.CREATOR),
                source.createTypedArrayList(UserLedger.CREATOR),
                source.createTypedArrayList(MyTeamResponse.CREATOR)
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeParcelable(user, 0)
            writeTypedList(userTarget)
            writeTypedList(distributorList)
            writeTypedList(dealerList)
            writeTypedList(categoryList)
            writeTypedList(productList)
            writeTypedList(dsrTempUserData)
            writeTypedList(dsrUserData)
            writeTypedList(cartList)
            writeTypedList(orderList)
            writeTypedList(orderPaymentList)
            writeTypedList(salesExpenseList)
            writeTypedList(notificationList)
            writeTypedList(targetSchemeList)
            writeTypedList(userLedgerList)
            writeTypedList(myTeamList)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Data> = object : Parcelable.Creator<Data> {
                override fun createFromParcel(source: Parcel): Data = Data(source)
                override fun newArray(size: Int): Array<Data?> = arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readParcelable<Message>(Message::class.java.classLoader),
            source.readParcelable<Data>(Data::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(status)
        writeParcelable(message, 0)
        writeParcelable(data, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GeneralResponse> = object : Parcelable.Creator<GeneralResponse> {
            override fun createFromParcel(source: Parcel): GeneralResponse = GeneralResponse(source)
            override fun newArray(size: Int): Array<GeneralResponse?> = arrayOfNulls(size)
        }
    }
}