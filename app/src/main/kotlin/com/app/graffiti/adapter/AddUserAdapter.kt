package com.app.graffiti.adapter

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.UserDsrData
import com.app.graffiti.utils.Common
import kotlinx.android.synthetic.main.item_user_dsr_data.view.*

/**
 * [AddUserAdapter] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 11/4/18
 */

class AddUserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val TAG = AddUserAdapter::class.java.simpleName
        val NULL_TYPE = 0
        val ITEM_TYPE = 1
    }

    var usersList: ArrayList<out UserDsrData> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NULL_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_null_layout, parent, false)
                return RecyclerAdapter.NullViewHolder(view)
            }
            ITEM_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_dsr_data, parent, false)
                return DealerHolder(view)
            }
            else -> {
                throw IllegalArgumentException("View Holder type is not compatible with any existing view holder")
            }
        }
    }

    override fun getItemCount(): Int = if (usersList.size == 0) 1 else usersList.size

    override fun getItemViewType(position: Int): Int =
            if (usersList.isEmpty()) {
                NULL_TYPE
            } else {
                ITEM_TYPE
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecyclerAdapter.NullViewHolder -> {
                holder.bindNullTitle("Add new user data\nSwipe left or right to delete")
            }
            is DealerHolder -> {
                holder.isBinding = true
                holder.bindData(usersList.get(holder.adapterPosition))
                holder.isBinding = false
                holder.editProfile?.setOnClickListener {
                    if (!holder.isBinding) {
                        val bundle = Bundle()
                        bundle.putParcelable(Common.EXTRA_DSR_DEALER, usersList.get(holder.adapterPosition))
                        itemClickListener?.onItemClick(bundle, holder.adapterPosition)
                    }
                }
            }
        }
    }

    fun removeItem(position: Int) {
        usersList.remove(usersList.get(position))
        notifyDataSetChanged()
    }

    class DealerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context by lazy {
            return@lazy itemView.context
        }
        val editProfile by lazy {
            return@lazy itemView.findViewById<AppCompatImageView>(R.id.item_user_dsr_imageView_editProfile)
        }
        var isBinding = true

        fun bindData(data: Any) {
            if (data is UserDsrData) {
                itemView?.item_user_dsr_textView_position?.text = "${adapterPosition + 1}."
                itemView?.item_user_dsr_textView_name?.text = "${data.firstName} ${data.lastName}"
                itemView?.item_user_dsr_textView_email?.text = data.email
                itemView?.item_user_dsr_textView_mobile?.text = data.mobileNumber
                itemView?.item_user_dsr_textView_location?.text = data.address
                itemView?.item_user_dsr_textView_description?.text = data.description
                itemView?.item_user_dsr_textView_firmName?.text = data.firmName
                when (data.status) {
                    "inactive" -> {
                        itemView?.item_user_dsr_textView_status?.setBackgroundColor(ContextCompat.getColor(
                                context,
                                android.R.color.darker_gray
                        ))
                        itemView?.item_user_dsr_textView_status?.text = data.status
                    }
                    "active" -> {
                        itemView?.item_user_dsr_textView_status?.setBackgroundColor(ContextCompat.getColor(
                                context,
                                android.R.color.holo_green_dark
                        ))
                        itemView?.item_user_dsr_textView_status?.text = data.status
                    }
                }
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(bundle: Bundle?, position: Int)
    }
}