package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * [OrderReportHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 23/4/18
 */

class OrderReportHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context by lazy {
        return@lazy itemView.context
    }

    fun bindOrderReportDetails(data: Any) {
        /*if (data is OrderItem) {
            itemView.item_order_report_textView_orderId?.text = "Order Id : ${data.id}"
            itemView.item_order_report_textView_orderDate?.text = Common.formatDateTime(
                    data.insertOrderDate,
                    "dd-MM-yyyy",
                    "EEE, dd MMM yyyy"
            )
            itemView.item_order_report_textView_orderFor?.text = "Ordered for : ${data.user?.firstName} ${data.user?.lastName}"
            itemView.item_order_report_textView_totalProducts?.text = "Total no. of products ordered : ${data.product?.size}"
            itemView.item_order_report_textView_orderTotal?.text = context?.getString(
                    R.string.text_total,
                    context?.getString(
                            R.string.currency_price,
                            data.totalAmount?.toString()
                    )
            )
        }*/
    }
}