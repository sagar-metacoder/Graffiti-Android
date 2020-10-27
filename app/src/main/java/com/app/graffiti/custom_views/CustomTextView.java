package com.app.graffiti.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * {@link CustomTextView} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @see AppCompatTextView
 * @since 11/4/18
 */

public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
