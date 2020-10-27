package com.app.mvvmcore.lifecycle_aware;

/**
 * {@link LifeCycleObserverImpl} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 3/4/18
 */

public interface LifeCycleObserverImpl {
    void onStateChanged(ActivityState activityState);
}
