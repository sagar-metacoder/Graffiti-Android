package com.app.graffiti.adapter

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.OrderItem
import com.app.graffiti.view_holder.OrderDetailHolder
import kotlinx.android.synthetic.main.item_null_layout.view.*
import kotlinx.android.synthetic.main.item_order_header.view.*

/**
 * [OrderListRecyclerAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 16/4/18
 */
// TODO : [ 15/6/18/Jeel Vankhede ] : OrderListRecyclerAdapter   Unused, delete once done
class OrderListRecyclerAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val TAG = OrderListRecyclerAdapter::class.java.simpleName
        val NULL_TYPE = 0
        val HEADER = 1
        val ITEM = 2
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
                                        R.layout.item_order_header,
                                        parent,
                                        false
                                )
                )
            }
            ITEM -> {
                OrderDetailHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(
                                        R.layout.item_product_order,
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
            if (multiTypeItem.get(position) is Header) HEADER else ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NullViewHolder -> {
                holder.bindNullTitle("No data found !")
            }
            is HeaderViewHolder -> {
                holder.bindHeader(multiTypeItem.get(holder.adapterPosition) as Header)
            }
            is OrderDetailHolder -> {
                holder.bindOrderDetail(multiTypeItem.get(holder.adapterPosition) as OrderItem)
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
            itemView?.item_order_header_textView_title?.setText(data.title)
            itemView?.item_order_header_textView_createDate?.setText(data.createDate)
            itemView?.item_order_header_textView_addedBy?.setText(data.addedBy)
        }
    }

    data class Header(
            val title: String,
            val addedBy: String,
            val createDate: String,
            val total: Double,
            val orderId: Int
    )

    interface MultiItemClickListener {
        fun onItemClicked(bundle: Bundle, @IdRes itemId: Int, position: Int)
    }
}