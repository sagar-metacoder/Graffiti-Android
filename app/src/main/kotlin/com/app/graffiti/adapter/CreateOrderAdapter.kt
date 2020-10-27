package com.app.graffiti.adapter

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.model.ProductOrderData
import com.app.graffiti.view_holder.ProductOrderDataHolder
import kotlinx.android.synthetic.main.item_null_layout.view.*
import kotlinx.android.synthetic.main.item_product_order_data.view.*

/**
 * [CreateOrderAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/4/18
 */

class CreateOrderAdapter(private val itemClickListener: ItemClickListener?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val TAG = CreateOrderAdapter::class.java.simpleName ?: "CreateOrderAdapter"
        const val DEFAULT_TYPE = 0
        const val ITEM_TYPE = 1
        const val EXTRA_PRODUCT_DATA = "product_order_data"
        const val EXTRA_VIEW_ID = "view_id"
    }

    private var productOrderData = ArrayList<ProductOrderData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DEFAULT_TYPE -> {
                DefaultViewHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_null_layout,
                                        parent,
                                        false
                                )
                )
            }
            ITEM_TYPE -> {
                ProductOrderDataHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_product_order_data,
                                        parent,
                                        false
                                )
                )
            }
            else -> {
                throw IllegalArgumentException("No suitable viewHolder found for your viewType $viewType")
            }
        }
    }

    override fun getItemCount(): Int = if (productOrderData.isEmpty()) 1 else productOrderData.size

    override fun getItemViewType(position: Int): Int = if (productOrderData.isEmpty()) DEFAULT_TYPE else ITEM_TYPE

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DefaultViewHolder -> {
                //holder.showDefaultTitle("Add product order data,\nSwipe Left/Right to remove")
            }
            is ProductOrderDataHolder -> {
                holder.bindProductData(productOrderData.elementAt(holder.adapterPosition))
                holder.itemView?.item_product_order_data_imageView_edit?.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(EXTRA_PRODUCT_DATA, productOrderData[holder.adapterPosition])
                    bundle.putInt(EXTRA_VIEW_ID, holder.itemView.item_product_order_data_imageView_edit.id)
                    itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                }
            }
            else -> {
            }
        }
    }

    fun addProduct(orderData: ProductOrderData) {
        var isUnique = true
        var index = 0
        if (productOrderData.isNotEmpty()) {
            for (i in 0 until productOrderData.size) {
                if (productOrderData[i].productId == orderData.productId) {
                    isUnique = false
                    index = i
                }
            }
        }
        if (isUnique) {
            productOrderData.add(orderData)
        } else {
            val productData = productOrderData[index]
            productData.productName = orderData.productName
            productData.productId = orderData.productId
            productData.quantity = orderData.quantity
            productData.price = orderData.price
            productData.total = orderData.total
        }
        notifyDataSetChanged()
    }

    fun removeProduct(orderData: ProductOrderData) {
        productOrderData.remove(orderData)
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): ProductOrderData = productOrderData.elementAt(position)

    fun getAllData(): List<ProductOrderData> = productOrderData.toList()

    class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun showDefaultTitle(data: Any) {
            if (data is String) {
                itemView.item_null_textView?.text = data
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(bundle: Bundle, position: Int)
    }
}
