package com.app.graffiti.view_holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.OrderPayment
import kotlinx.android.synthetic.main.item_payment_detail.view.*

/**
 * [PaymentDetailHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 17/4/18
 */

class PaymentDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val context: Context? by lazy {
        return@lazy itemView.context
    }

    fun bindPaymentDetail(data: Any) {
        /*if (data is OrderPayment.Payment) {
            itemView.item_payment_detail_textView_date?.setText("Added @ ${Common.formatDateTime(
                    data.insertDate,
                    "dd-MM-yyyy",
                    "EEE, dd MMM yy"
            )}")
            itemView.item_payment_detail_textView_amount?.setText("Amount : ${context.getString(R.string.currency_price, data.amount.toString())}")
            itemView.item_payment_detail_textView_description?.setText(data.description)
        }*/
        if (data is OrderPayment) {
            itemView?.item_payment_detail_textView_date?.text = context?.getString(
                    R.string.item_payment_history_date,
                    data.date
            )
            itemView?.item_payment_detail_textView_amount?.text = context?.let {
                it.getString(
                        R.string.item_payment_history_amount,
                        it.getString(
                                R.string.currency_price,
                                data.amount
                        )
                )
            }
            itemView?.item_payment_detail_textView_description?.text = context?.getString(
                    R.string.item_payment_history_description,
                    data.description
            )
        }
    }
}