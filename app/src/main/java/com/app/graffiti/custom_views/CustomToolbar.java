package com.app.graffiti.custom_views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.app.graffiti.utils.Common;

/**
 * {@link CustomToolbar} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @see Toolbar
 * @since 27/3/18
 */

public class CustomToolbar extends Toolbar {
    public static final String TAG = CustomToolbar.class.getSimpleName();

    private TextView titleView;

    public CustomToolbar(Context context) {
        this(context, null);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initToolbar();
    }

    private void initToolbar() {
        ColorDrawable drawable = (ColorDrawable) this.getBackground();
        if (drawable != null) {
            Log.e(TAG, " initToolbar : " + Common.isColorDark(drawable.getColor()));
            Common.changeToolbarTheme(this, drawable);
            /*if (Common.isColorDark(drawable.getColor())) {
                this.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
            } else {
                this.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
            }*/
        }
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        Common.changeToolbarTheme(this, color);
        postInvalidate();
    }
}
