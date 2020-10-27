package com.app.mvvmcore;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.app.mvvmcore.core.ViewModelHolder;
import com.app.mvvmcore.core.ViewModelStore;
import com.app.mvvmcore.lifecycle_aware.ActivityState;

import java.lang.ref.WeakReference;

/**
 * {@link ViewModelRegistry} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

public class ViewModelRegistry implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = ViewModelRegistry.class.getSimpleName();
    private static ViewModelRegistry sViewModelRegistry;
    private static ViewModelStore viewModelStore;

    private ViewModelRegistry() {
    }

    public static ViewModelRegistry of(Application application) {
        if (sViewModelRegistry == null) {
            synchronized (ViewModelRegistry.class) {
                sViewModelRegistry = new ViewModelRegistry();
                WeakReference<Application> applicationReference = new WeakReference<>(application);
                applicationReference.get().registerActivityLifecycleCallbacks(sViewModelRegistry);
                viewModelStore = ViewModelStore.init();
                return sViewModelRegistry;
            }
        }
        return sViewModelRegistry;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            viewModelStore.put(new ViewModelHolder(activity.getClass().getName(), activity));
        }
        viewModelStore.setState(activity.getClass(), ActivityState.ON_ACTIVIY_CREATED);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        viewModelStore.setState(activity.getClass(), ActivityState.ON_ACTIVIY_STARTED);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        viewModelStore.setState(activity.getClass(), ActivityState.ON_ACTIVIY_RESUMED);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        viewModelStore.setState(activity.getClass(), ActivityState.ON_ACTIVIY_PAUSED);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        viewModelStore.setState(activity.getClass(), ActivityState.ON_ACTIVIY_STOPPED);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        viewModelStore.setState(activity.getClass(), ActivityState.ON_ACTIVIY_SAVED_INSTANCE);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        viewModelStore.setState(activity.getClass(), ActivityState.ON_ACTIVIY_DESTROYED);
    }
}
