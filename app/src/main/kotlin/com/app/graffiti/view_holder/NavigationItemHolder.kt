package com.app.graffiti.view_holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.model.NavigationItem
import kotlinx.android.synthetic.main.item_navigations.view.*

/**
 * [NavigationItemHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 18/6/18
 */

class NavigationItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context: Context? by lazy {
        return@lazy itemView.context
    }

    fun bindItems(data: Any?) {
        if (data != null && data is NavigationItem) {
            itemView?.item_navigation_imageView?.setImageResource(data.imageResource)
            itemView?.item_navigation_textView?.text = data.title
        }
    }
}