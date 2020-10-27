package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.R
import com.app.graffiti.model.ProductOrderData
import kotlinx.android.synthetic.main.item_product_order_data.view.*

/**
 * [ProductOrderDataHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 19/4/18
 */

class ProductOrderDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindProductData(data: Any) {
        if (data is ProductOrderData) {
            itemView?.item_product_order_data_textView_srNo?.text = "${adapterPosition + 1}"
            itemView?.item_product_order_data_textView_productName?.text = "${data.productName}"
            itemView?.item_product_order_data_textView_productQuantity?.text = "Qty : ${data.quantity}"
            itemView?.item_product_order_data_textView_productMrp?.text = "Mrp : ${itemView.context.getString(R.string.currency_price, data.price.toString())}"
            itemView?.item_product_order_data_textView_productTotal?.text = "Total : ${itemView.context.getString(R.string.currency_price, data.total.toString())}"
        }
    }
}