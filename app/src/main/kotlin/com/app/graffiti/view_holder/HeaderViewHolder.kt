package com.app.graffiti.view_holder

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.widget.ImageView
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.MultiItemList
import kotlinx.android.synthetic.main.item_header.view.*

/**
 * [HeaderViewHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 17/4/18
 */

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val deleteImage by lazy {
        return@lazy itemView.findViewById<ImageView>(R.id.item_header_imageView_deleteItem)
    }

    fun setHeader(data: Any) {
        if (data is String) {

        }
        if (data is MultiItemList) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                itemView.item_header_textView_title?.setText(
                        Html.fromHtml(data.title, Html.FROM_HTML_MODE_COMPACT)
                )
            } else {
                itemView.item_header_textView_title?.setText(
                        Html.fromHtml(data.title)
                )
            }
            if (data.isDeleteable == true) {
                itemView.item_header_imageView_deleteItem?.visibility = View.VISIBLE
            } else {
                itemView.item_header_imageView_deleteItem?.visibility = View.GONE
            }
        }
    }
}