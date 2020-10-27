package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.TargetScheme
import com.app.graffiti.utils.Common
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_target_scheme.view.*

/**
 * [TargetSchemeHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 25/6/18
 */

class TargetSchemeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context by lazy {
        return@lazy itemView.context
    }

    fun bindTargetScheme(data: Any?) {
        if (data != null && data is TargetScheme) {
            data.image?.let {
                Glide.with(context)
                        .load(it)
                        .apply(
                                RequestOptions()
                                        .error(R.drawable.no_image)
                                        .placeholder(R.drawable.no_image)
                                        .override(
                                                itemView.item_target_scheme_imageView?.width?:0,
                                                itemView.item_target_scheme_imageView?.height?:0
                                        )
                        )
                        .into(itemView.item_target_scheme_imageView)
            }
            data.title?.let {
                itemView.item_target_scheme_textView_title?.text = "Title : $it"
            }
            data.description?.let {
                itemView.item_target_scheme_textView_description?.text = "Description : $it"
            }
            data.startDate?.let {
                itemView.item_target_scheme_textView_startDate?.text = "Start date : ${Common.formatDateTime(it,"yyyy-MM-dd","EEE, dd MMM-yy")}"
            }
            data.endDate?.let {
                itemView.item_target_scheme_textView_endDate?.text = "End date : ${Common.formatDateTime(it,"yyyy-MM-dd","EEE, dd MMM-yy")}"
            }
        }
    }
}