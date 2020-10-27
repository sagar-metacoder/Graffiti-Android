package com.app.graffiti.view_holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.Category
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_product_subcategory.view.*

/**
 * [SubCategoryViewHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindSubCategories(data: Any) {
        if (data is String) {
            itemView.item_product_subcategory_textView_name?.text = data
        }
        if (data is Category) {
            itemView.item_product_subcategory_textView_name?.text = data?.category
            Glide
                    .with(itemView.item_product_subcategory_imageView.context)
                    .load(data.imagePath)
                    .apply(
                            RequestOptions()
                                    .error(R.drawable.no_image)
                                    .placeholder(R.drawable.no_image)
                                    .override(
                                            itemView.item_product_subcategory_imageView?.width ?: 0,
                                            itemView.item_product_subcategory_imageView?.height ?: 0
                                    )
                    )
                    .into(itemView.item_product_subcategory_imageView)
        }
    }
}
