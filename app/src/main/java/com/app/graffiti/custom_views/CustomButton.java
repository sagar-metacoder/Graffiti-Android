package com.app.graffiti.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;

import com.app.graffiti.R;
import com.app.graffiti.utils.Common;

/**
 * {@link CustomButton} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @see AppCompatButton
 * @since 27/3/18
 */

public class CustomButton extends AppCompatButton {
    public static final String TAG = CustomButton.class.getSimpleName();
    private boolean ignoreTextColor;
    private boolean ignoreTextGravity;

    public CustomButton(Context context) {
        this(context, null);
    }

    public CustomButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton, 0, 0);
        for (int index = 0; index < typedArray.getIndexCount(); index++) {
            int attr = typedArray.getIndex(index);
            switch (attr) {
                case R.styleable.CustomButton_ignoreTextColor: {
                    ignoreTextColor = typedArray.getBoolean(attr, false);
                    break;
                }
                case R.styleable.CustomButton_ignoreTextGravity: {
                    ignoreTextGravity = typedArray.getBoolean(attr, false);
                    break;
                }
                default: {
                    break;
                }
            }
        }
        typedArray.recycle();
        seUpButton();
    }

    private void seUpButton() {
        int leftPixels = (int) Common.dpToPixel(getContext(), 10);
        int topPixels = (int) Common.dpToPixel(getContext(), 0);
        int bottomPixels = (int) Common.dpToPixel(getContext(), 0);
        int rightPixels = (int) Common.dpToPixel(getContext(), 10);
        setPadding(leftPixels, topPixels, rightPixels, bottomPixels);
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(Common.dpToPixel(this.getContext(), 4));
        BitmapDrawable bitmapDrawable = null;
        if (this.getBackground() != null) {
            if (this.getBackground() instanceof BitmapDrawable) {
                bitmapDrawable = (BitmapDrawable) this.getBackground();
                Palette palette = Palette.from(bitmapDrawable.getBitmap()).generate();
                setTextColorAccToBg(
                        palette.getVibrantColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimaryDark))
                );
            }
            if (this.getBackground() instanceof ColorDrawable) {
                backgroundDrawable.setColor(((ColorDrawable) this.getBackground()).getColor());
                if (!ignoreTextColor) {
                    ColorDrawable colorDrawable = (ColorDrawable) this.getBackground();
                    setTextColorAccToBg(colorDrawable.getColor());
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(Common.dpToPixel(this.getContext(), 2));
        } else {
            ViewCompat.setElevation(this, Common.dpToPixel(this.getContext(), 2));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (this.getBackground() != null) {
                if (this.getBackground() instanceof BitmapDrawable && bitmapDrawable != null) {
                    setBackground(bitmapDrawable);
                } else {
                    setBackground(backgroundDrawable);
                }
            }
        }
        if (!ignoreTextGravity) {
            setGravity(Gravity.CENTER);
        }
    }

    private void setTextColorAccToBg(int bgColor) {
        if (!ignoreTextColor) {
            if (Common.isColorDark(bgColor)) {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            } else {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = (int) Common.dpToPixel(getContext(), 48) + getPaddingTop() + getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }
}
