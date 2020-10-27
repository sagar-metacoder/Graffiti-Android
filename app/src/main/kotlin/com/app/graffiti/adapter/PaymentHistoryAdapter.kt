package com.app.graffiti.adapter

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.OrderPayment
import com.app.graffiti.view_holder.PaymentDetailHolder
import kotlinx.android.synthetic.main.item_no_payment.view.*
import kotlinx.android.synthetic.main.item_null_layout.view.*
import kotlinx.android.synthetic.main.item_payment_footer.view.*
import kotlinx.android.synthetic.main.item_payment_header.view.*

/**
 * [PaymentHistoryAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 16/4/18
 */

class PaymentHistoryAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val TAG = PaymentHistoryAdapter::class.java.simpleName
        val NULL_TYPE = 0
        val HEADER = 1
        val ITEM = 2
        val FOOTER = 3
        val NO_ITEM = 4
    }

    var multiTypeItem: ArrayList<in Any> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var multiClickListener: MultiItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NULL_TYPE -> {
                NullViewHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_null_layout,
                                        parent,
                                        false
                                )
                )
            }
            HEADER -> {
                HeaderViewHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_payment_header,
                                        parent,
                                        false
                                )
                )
            }
            ITEM -> {
                PaymentDetailHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_payment_detail,
                                        parent,
                                        false
                                )
                )
            }
            NO_ITEM -> {
                NoPaymentViewHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_no_payment,
                                        parent,
                                        false
                                )
                )
            }
            FOOTER -> {
                FooterViewHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_payment_footer,
                                        parent,
                                        false
                                )
                )
            }
            else -> {
                throw IllegalArgumentException("Item type ($viewType) passed to adapter is invalid")
            }
        }
    }

    override fun getItemCount(): Int = if (multiTypeItem.isEmpty()) 1 else multiTypeItem.size

    override fun getItemViewType(position: Int): Int {
        return if (multiTypeItem.isEmpty())
            NULL_TYPE
        else {
            when (multiTypeItem.get(position)) {
                is Header -> {
                    HEADER
                }
                is Footer -> {
                    FOOTER
                }
                is NoPayment -> {
                    NO_ITEM
                }
                else -> {
                    ITEM
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NullViewHolder -> {
                holder.bindNullTitle("No history found !")
            }
            is HeaderViewHolder -> {
                holder.bindHeader(multiTypeItem.get(holder.adapterPosition) as Header)
            }
            is PaymentDetailHolder -> {
                holder.bindPaymentDetail(multiTypeItem.get(holder.adapterPosition) as OrderPayment)
            }
            is NoPaymentViewHolder -> {
                holder.bindTitle(multiTypeItem.get(holder.adapterPosition) as NoPayment)
            }
            is FooterViewHolder -> {
                holder.bindFooter(multiTypeItem.get(holder.adapterPosition) as Footer)
            }
        }
    }

    class NullViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindNullTitle(message: String) {
            itemView.item_null_textView?.text = message
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindHeader(data: Header) {
            itemView?.item_payment_header_textView_title?.setText(data.title)
            itemView?.item_payment_header_textView_orderId?.setText("Order id : ${data.orderId}")
            itemView?.item_payment_header_textView_createDate?.setText(data.createDate)
            itemView?.item_payment_header_textView_addedBy?.setText(data.addedBy)
        }
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context by lazy {
            return@lazy itemView.context
        }

        fun bindFooter(data: Footer) {
            itemView?.item_payment_footer_textView_total?.setText("Total : ${context.getString(R.string.currency_price, data.total.toString())}")
            itemView?.item_payment_footer_textView_pending?.setText("Pending : ${context.getString(R.string.currency_price, data.pending.toString())}")
        }
    }

    class NoPaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTitle(message: NoPayment) {
            itemView.item_no_payment_textView?.text = message.title
        }
    }

    data class Header(
            val title: String,
            val addedBy: String,
            val createDate: String,
            val orderId: Int
    )

    data class Footer(
            val total: Double,
            val pending: Double
    )

    data class NoPayment(
            val title: String
    )

    interface MultiItemClickListener {
        fun onItemClicked(bundle: Bundle, @IdRes itemId: Int, position: Int)
    }
}