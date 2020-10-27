package com.app.graffiti.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.UserLedger
import com.app.graffiti.utils.Common
import kotlinx.android.synthetic.main.item_ledger_footer.view.*
import kotlinx.android.synthetic.main.item_ledger_header.view.*
import kotlinx.android.synthetic.main.item_ledger_item.view.*
import kotlinx.android.synthetic.main.item_ledger_no_item.view.*
import kotlinx.android.synthetic.main.item_null_layout.view.*

/**
 * [UserLedgerAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/6/18
 */

class UserLedgerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val TAG = UserLedgerAdapter::class.java.simpleName ?: "UserLedgerAdapter"
    }

    var mLedgerList = ArrayList<UserLedger>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AdapterType.NULL_TYPE.getAdapterType() -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_null_layout, parent, false)
                NullViewHolder(view)
            }
            AdapterType.HEADER_TYPE.getAdapterType() -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ledger_header, parent, false)
                HeaderViewHolder(view)
            }
            AdapterType.ITEM_TYPE.getAdapterType() -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ledger_item, parent, false)
                LedgerViewHolder(view)
            }
            AdapterType.FOOTER_TYPE.getAdapterType() -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ledger_footer, parent, false)
                FooterViewHolder(view)
            }
            AdapterType.NO_ITEM_TYPE.getAdapterType() -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ledger_no_item, parent, false)
                NoItemViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_null_layout, parent, false)
                NullViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = if (mLedgerList.isEmpty()) 1 else mLedgerList.size

    override fun getItemViewType(position: Int): Int {
        return if (mLedgerList.isEmpty())
            AdapterType.NULL_TYPE.getAdapterType()
        else {
            when (mLedgerList[position].page) {
                AdapterType.HEADER_TYPE.getViewType() -> {
                    AdapterType.HEADER_TYPE.getAdapterType()
                }
                AdapterType.ITEM_TYPE.getViewType() -> {
                    AdapterType.ITEM_TYPE.getAdapterType()
                }
                AdapterType.FOOTER_TYPE.getViewType() -> {
                    AdapterType.FOOTER_TYPE.getAdapterType()
                }
                AdapterType.NO_ITEM_TYPE.getViewType() -> {
                    AdapterType.NO_ITEM_TYPE.getAdapterType()
                }
                else -> {
                    AdapterType.ITEM_TYPE.getAdapterType()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NullViewHolder -> {
                holder.bindNullHolder("No ledger details found")
            }
            is HeaderViewHolder -> {
                holder.bindHeaderHolder(mLedgerList[holder.adapterPosition])
            }
            is LedgerViewHolder -> {
                holder.bindLedgerHolder(mLedgerList[holder.adapterPosition])
            }
            is FooterViewHolder -> {
                holder.bindFooterHolder(mLedgerList[holder.adapterPosition])
            }
            is NoItemViewHolder -> {
                holder.bindNoItems(mLedgerList[holder.adapterPosition])
            }
        }
    }

    private class NullViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindNullHolder(message: String) {
            itemView.item_null_textView?.text = message
        }
    }

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context by lazy {
            return@lazy itemView.context
        }

        fun bindHeaderHolder(data: UserLedger?) {
            data?.let {
                itemView.item_ledger_header_textView_openingBalance?.text =
                        context.getString(R.string.currency_price, it.totalAmount?.toString())
            }
        }
    }

    private class LedgerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context by lazy {
            return@lazy itemView.context
        }

        fun bindLedgerHolder(data: UserLedger?) {
            data?.let {
                itemView.item_ledger_item_textView_index?.text = "$adapterPosition.)"
                itemView.item_ledger_item_textView_date?.text =
                        Common.formatDateTime(it.date, "dd-MM-yyyy", "EEE, dd MMM yyyy")
                itemView.item_ledger_item_textView_description?.text = it.description
                itemView.item_ledger_item_textView_amount?.text =
                        context.getString(R.string.currency_price, it.amount)
            }
        }
    }

    private class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context by lazy {
            return@lazy itemView.context
        }

        fun bindFooterHolder(data: UserLedger?) {
            data?.let {
                itemView.item_ledger_footer_textView_closingBalance?.text =
                        context.getString(R.string.currency_price, it.pendingAmount?.toString())
            }
        }
    }

    private class NoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindNoItems(data: UserLedger?) {
            data?.let {
                itemView.item_ledger_no_item_textView?.text =
                        it.description
            }
        }
    }

    enum class AdapterType constructor(
            private val adapterType: Int,
            private val viewType: String
    ) {
        NULL_TYPE(
                0,
                "NULL_TYPE"
        ),
        HEADER_TYPE(
                1,
                "header"
        ),
        ITEM_TYPE(
                2,
                "middle"
        ),
        FOOTER_TYPE(
                3,
                "footer"
        ),
        NO_ITEM_TYPE(
                4,
                "no_items"
        );

        fun getAdapterType(): Int = adapterType

        fun getViewType(): String = viewType
    }
}