package com.app.graffiti.view_holder

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.NotificationData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_notifications.view.*
import com.vatsal.imagezoomer.ZoomAnimation



/**
 * [NotificationsViewHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 20/6/18
 */

class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context: Context? by lazy {
        return@lazy itemView.context
    }

    fun bindNotifications(data: Any?) {
        if (data != null && data is NotificationData) {
            context?.let {
                itemView?.item_notification_imageView?.let { imageView ->
                    Glide.with(it)
                            .load(data.image)
                            .apply(
                                    RequestOptions()
                                            .override(imageView.width, imageView.height)
                                            .error(R.drawable.no_image)
                                            .placeholder(R.drawable.no_image)
                                            .centerCrop()
                            )
                            .into(imageView)
                }
            }

            itemView?.item_notification_textView_title?.text = data.title
            itemView?.item_notification_textView_description?.text = data.description
        }
    }
}