package com.app.mvvmcore.lifecycle_aware;


/**
 * {@link LiveData} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

public interface LiveData<T extends Object> {
    public void onNewData(T t);

    public void onCleared(T t);
}
