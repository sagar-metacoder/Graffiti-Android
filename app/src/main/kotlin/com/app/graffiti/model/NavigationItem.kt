package com.app.graffiti.model

import android.support.annotation.DrawableRes

/**
 * [NavigationItem] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 18/6/18
 */

data class NavigationItem(
        val title: String,
        @DrawableRes
        val imageResource: Int
)