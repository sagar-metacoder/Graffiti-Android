package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_dashboard.view.*

/**
 * [DashboardViewHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 28/3/18
 */

class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindDasboardView(data: Any) {
        if (data is String) {
            itemView.item_dashboard_textView_itemName?.text = data
        }
    }
}