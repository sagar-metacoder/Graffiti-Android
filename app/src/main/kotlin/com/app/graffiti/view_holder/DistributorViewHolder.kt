package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.model.webresponse.ChildUser
import com.app.graffiti.utils.Common
import kotlinx.android.synthetic.main.item_distributor.view.*

/**
 * [DistributorViewHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class DistributorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindDistributorView(data: Any?) {
        if (data is String) {
            itemView.item_distributor_textView_firmName?.text = data
        }
        if (data != null && data is ChildUser) {
            itemView.item_distributor_textView_firmName?.text = data.firmName
            itemView.item_distributor_textView_distributorJoinDate?.text = "Join @ ${Common.formatDateTime(
                    data.date,
                    "yyyy-MM-dd",
                    "MMM dd, yyyy"
            )}"
        }
    }
}