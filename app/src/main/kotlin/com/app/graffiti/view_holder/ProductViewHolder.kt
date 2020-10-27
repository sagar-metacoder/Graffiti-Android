package com.app.graffiti.view_holder

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.app.graffiti.R
import com.app.graffiti.model.webresponse.Product
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_product.view.*

/**
 * [ProductViewHolder] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 30/3/18
 */

class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mainContainer by lazy {
        return@lazy itemView.findViewById<ConstraintLayout>(R.id.item_product_mainContainer)
    }
    val addToCartButton by lazy {
        return@lazy itemView.findViewById<TextView>(R.id.item_product_textView_addToCart)
    }

    fun bindProducts(data: Any) {
        if (data is String) {
            itemView.item_product_textView_name?.text = data
        }
        if (data is Product) {
            itemView.item_product_textView_name?.text = data?.productName
            Glide
                    .with(itemView.item_product_imageView.context)
                    .load(data.imagePath)
                    .apply(
                            RequestOptions()
                                    .error(R.drawable.no_image)
                                    .placeholder(R.drawable.no_image)
                                    .override(
                                            itemView?.item_product_imageView?.width ?: 0,
                                            itemView?.item_product_imageView?.height ?: 0
                                    )
                    )
                    .into(itemView.item_product_imageView)
            if (data.shouldShowCart) itemView.item_product_textView_addToCart.visibility = View.VISIBLE
            else itemView.item_product_textView_addToCart.visibility = View.GONE
        }
    }
}
