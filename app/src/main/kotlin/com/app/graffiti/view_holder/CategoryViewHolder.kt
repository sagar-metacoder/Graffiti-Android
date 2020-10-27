package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.model.webresponse.Category
import kotlinx.android.synthetic.main.item_product_category.view.*

/**
 * [CategoryViewHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCategories(data: Any) {
        if (data is String) {
            itemView.item_product_category_textView_categoryName?.text = data
        }
        if (data is Category) {
            itemView.item_product_category_textView_categoryName?.text = data?.category
        }
    }
}
