package com.app.mvvmcore.lifecycle_aware;


/**
 * {@link ActivityState} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

public enum ActivityState {
    ON_ACTIVIY_CREATED,
    ON_ACTIVIY_STARTED,
    ON_ACTIVIY_RESUMED,
    ON_ACTIVIY_PAUSED,
    ON_ACTIVIY_STOPPED,
    ON_ACTIVIY_DESTROYED,
    ON_ACTIVIY_SAVED_INSTANCE;
}
