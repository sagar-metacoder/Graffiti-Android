package com.app.graffiti.model.webresponse

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [OrderItem] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 20/4/18
 */

class OrderItem(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("product_name")
        val productName: String?,
        @SerializedName("item_code")
        val itemCode: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("total_product")
        val totalProduct: Int?,
        @SerializedName("dispatched_product")
        val dispatchedProduct: Int?
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(productName)
        writeString(itemCode)
        writeString(image)
        writeValue(totalProduct)
        writeValue(dispatchedProduct)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderItem> = object : Parcelable.Creator<OrderItem> {
            override fun createFromParcel(source: Parcel): OrderItem = OrderItem(source)
            override fun newArray(size: Int): Array<OrderItem?> = arrayOfNulls(size)
        }
    }
}

// TODO : [ 15/6/18/Jeel Vankhede ] : OrderItem   Remove once done
/*
class OrderItem(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("creator_id")
        val creatorId: Int?,
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("order_type")
        val orderType: String?,
        @SerializedName("base_amount")
        val baseAmount: Double?,
        @SerializedName("discount")
        val discount: Double?,
        @SerializedName("tax")
        val tax: Double?,
        @SerializedName("total_amount")
        val totalAmount: Double?,
        @SerializedName("delivery_date")
        val deliveryDate: String?,
        @SerializedName("insert_order_date")
        val insertOrderDate: String?,
        @SerializedName("insert_complete_date")
        val insertCompleteDate: String?,
        @SerializedName("creator")
        val creator: User?,
        @SerializedName("user")
        val user: User?,
        @SerializedName("product_list")
        val product: List<Product>?
) : Parcelable {
    class User(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("first_name")
            val firstName: String?,
            @SerializedName("last_name")
            val lastName: String?,
            @SerializedName("email")
            val email: String?
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readString(),
                source.readString(),
                source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeValue(id)
            writeString(firstName)
            writeString(lastName)
            writeString(email)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
                override fun createFromParcel(source: Parcel): User = User(source)
                override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
            }
        }
    }

    class Product(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("order_id")
            val orderId: Int?,
            @SerializedName("product_id")
            val productId: Int?,
            @SerializedName("quantity")
            val quantity: Int?,
            @SerializedName("price")
            val price: Double?,
            @SerializedName("total")
            val total: Double?,
            @SerializedName("product_name")
            val productName: String?,
            @SerializedName("item_code")
            val itemCode: String?,
            @SerializedName("image_path")
            val imagePath: String?,
            @SerializedName("main_category_id")
            val mainCategoryId: Int?,
            @SerializedName("sub_category_id")
            val subCategoryId: Int?,
            @SerializedName("main_category")
            val mainCategory: String?,
            @SerializedName("sub_category")
            val subCategory: String?
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Double::class.java.classLoader) as Double?,
                source.readValue(Double::class.java.classLoader) as Double?,
                source.readString(),
                source.readString(),
                source.readString(),
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readString(),
                source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeValue(id)
            writeValue(orderId)
            writeValue(productId)
            writeValue(quantity)
            writeValue(price)
            writeValue(total)
            writeString(productName)
            writeString(itemCode)
            writeString(imagePath)
            writeValue(mainCategoryId)
            writeValue(subCategoryId)
            writeString(mainCategory)
            writeString(subCategory)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Product> = object : Parcelable.Creator<Product> {
                override fun createFromParcel(source: Parcel): Product = Product(source)
                override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readValue(Double::class.java.classLoader) as Double?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readParcelable<User>(User::class.java.classLoader),
            source.readParcelable<User>(User::class.java.classLoader),
            source.createTypedArrayList(Product.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeValue(creatorId)
        writeValue(userId)
        writeString(orderType)
        writeValue(baseAmount)
        writeValue(discount)
        writeValue(tax)
        writeValue(totalAmount)
        writeString(deliveryDate)
        writeString(insertOrderDate)
        writeString(insertCompleteDate)
        writeParcelable(creator, 0)
        writeParcelable(user, 0)
        writeTypedList(product)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderItem> = object : Parcelable.Creator<OrderItem> {
            override fun createFromParcel(source: Parcel): OrderItem = OrderItem(source)
            override fun newArray(size: Int): Array<OrderItem?> = arrayOfNulls(size)
        }
    }
}*/