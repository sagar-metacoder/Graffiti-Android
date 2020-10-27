package com.app.graffiti;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * {@link BaseActivity} : <p>
 * Base Activity used to extend all your activity to ease your code and clean up all repeated junk for activities,
 * <p>
 * </br> Extend this activity instead of {@link AppCompatActivity} to minify mistakes like forgetting {@link #setContentView(View)} when creating new activity
 * <p>
 * </br> and some general work around like {@link #getSupportActionBar()} and {@link #getSupportFragmentManager()} etc.
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @see AppCompatActivity
 * @see #setContentView(View)
 * @since 6/2/18
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();
    private ActivityConfigs configs;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configs = setUpBaseActivity();
        if (configs.layoutResId != -1) {
            setContentView(configs.layoutResId);
        }
        if (configs.shouldSupportActionBar) {
            if (provideToolbar() != null) {
                setSupportActionBar(provideToolbar());
            }
        }
        onBaseCreated(savedInstanceState);
//        Log.e(TAG, " onCreate : Activity Cannonical Name \"" + ((Activity) this).getClass().getCanonicalName() + "\"");
    }

    public abstract ActivityConfigs setUpBaseActivity();

    public abstract void onBaseCreated(@Nullable Bundle savedInstanceState);

    public abstract @Nullable
    Toolbar provideToolbar();

    public void setUpToolbar(@Nullable CharSequence title, boolean displayHomeAsUp) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(displayHomeAsUp);
            getSupportActionBar().setDisplayShowHomeEnabled(displayHomeAsUp);
            if (title != null) {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    public static class ActivityConfigs {
        @LayoutRes
        private int layoutResId;
        private boolean shouldSupportActionBar = false;

        private ActivityConfigs(@LayoutRes int layoutResId, boolean shouldSupportActionBar) {
            this.layoutResId = layoutResId;
            this.shouldSupportActionBar = shouldSupportActionBar;
        }

        private ActivityConfigs(Builder builder) {
            this.layoutResId = builder.layoutResId;
        }

        public static class Builder {
            @LayoutRes
            private int layoutResId = -1;
            private boolean shouldSupportActionBar = false;

            public Builder(int layoutResId) {
                this.layoutResId = layoutResId;
            }

            public Builder shouldSetSupportActionBar(boolean shouldSupportActionBar) {
                this.shouldSupportActionBar = shouldSupportActionBar;
                return this;
            }

            public ActivityConfigs create() {
                /*if (configs.layoutResId == -1) {
                    throw new IllegalArgumentException("Layout resource id must not be empty");
                }*/
                return new ActivityConfigs(layoutResId, shouldSupportActionBar);
            }
        }
    }
}