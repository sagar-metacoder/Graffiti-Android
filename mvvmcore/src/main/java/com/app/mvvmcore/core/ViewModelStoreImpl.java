package com.app.mvvmcore.core;

import android.app.Activity;

import com.app.mvvmcore.lifecycle_aware.ActivityState;

/**
 * {@link ViewModelStoreImpl} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 3/4/18
 */

public interface ViewModelStoreImpl {
    void put(ViewModelHolder viewModelHolder);

    ViewModelHolder get(Class<? extends Activity> key);

    void setState(Class<? extends Activity> key, ActivityState activityState);
}
