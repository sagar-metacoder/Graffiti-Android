package com.app.mvvmcore.core;

import android.util.Log;

import com.app.mvvmcore.lifecycle_aware.ActivityState;
import com.app.mvvmcore.lifecycle_aware.LifeCycleObserverImpl;

/**
 * {@link ViewModel} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

public abstract class ViewModel implements LifeCycleObserverImpl {
    private static final String TAG = ViewModel.class.getSimpleName();

    public abstract boolean onClear();

    @Override
    public void onStateChanged(ActivityState activityState) {
        Log.i(TAG, " onStateChanged : activity state " + activityState.name());
    }
}
