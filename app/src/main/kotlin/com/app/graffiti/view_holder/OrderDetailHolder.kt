package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.MultiItemList
import com.app.graffiti.model.webresponse.OrderItem
import kotlinx.android.synthetic.main.item_order.view.*

/**
 * [OrderDetailHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 17/4/18
 */

class OrderDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val context by lazy {
        return@lazy itemView.context
    }

    fun bindOrderDetail(data: Any) {
        if (data is MultiItemList) {
            itemView.item_order_textView_productName?.setText("Product Name : ${data.itemData?.productName}")
            itemView.item_order_textView_productCode?.setText("Product Code : ${data.itemData?.itemCode}")
            itemView.item_order_textView_productQuantity?.setText("Quantity : ${data.itemData?.quantity}")
            /*Glide.with(itemView.item_order_imageView_product.context)
                    .load(data.itemData?.productImage ?: "")
                    .apply(
                            RequestOptions()
                                    .override(
                                            itemView.item_order_imageView_product?.width ?: 0,
                                            itemView.item_order_imageView_product?.height ?: 0
                                    )
                                    .placeholder(R.drawable.no_image)
                                    .error(R.drawable.no_image)
                    )
                    .into(itemView.item_order_imageView_product)*/
        }
        if (data is OrderItem) {
            itemView.item_order_textView_productName?.text = context.getString(
                    R.string.item_order_product_name,
                    data.productName
            )
            itemView.item_order_textView_productCode?.text = context.getString(
                    R.string.item_order_product_code,
                    data.itemCode
            )
            itemView.item_order_textView_productQuantity?.text = context.getString(
                    R.string.item_order_total_quantity,
                    data.totalProduct?.toString()
            )
            itemView.item_order_textView_dispatchedQuantity?.text = context.getString(
                    R.string.item_order_dispatched_quantity,
                    data.dispatchedProduct?.toString()
            )
            itemView.item_order_textView_pendingQuantity?.text = context.getString(
                    R.string.item_order_pending_quantity,
                    data.totalProduct?.minus(data.dispatchedProduct ?: 0)?.toString()
            )
        }
        /*if (data is OrderItem.Product) {
            itemView.item_product_order_textView_productName?.setText(data.productName)
            itemView.item_product_order_textView_productQuantity?.setText("Qty : ${data.quantity}")
            itemView.item_product_order_textView_productMrp?.setText(context?.getString(
                    R.string.text_mrp,
                    context?.getString(
                            R.string.currency_price,
                            data.price.toString()
                    )
            ))
            itemView.item_product_order_textView_productTotal?.setText(context?.getString(
                    R.string.text_total,
                    context?.getString(
                            R.string.currency_price,
                            data.total.toString()
                    )
            ))
            Glide.with(context)
                    .load(data.imagePath ?: "")
                    .apply(
                            RequestOptions()
                                    .override(
                                            itemView.item_product_order_imageView_product?.width
                                                    ?: 0,
                                            itemView.item_product_order_imageView_product?.height
                                                    ?: 0
                                    )
                                    .placeholder(R.drawable.no_image)
                                    .error(R.drawable.no_image)
                    )
                    .into(itemView.item_product_order_imageView_product)
        }*/
    }
}