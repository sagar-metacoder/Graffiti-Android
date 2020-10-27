package com.app.mvvmcore.core;


import android.app.Activity;
import android.util.Log;

import com.app.mvvmcore.lifecycle_aware.ActivityState;
import com.app.mvvmcore.lifecycle_aware.LifeCycleObserverImpl;

import java.util.HashMap;

/**
 * {@link ViewModelStore} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

public class ViewModelStore implements ViewModelStoreImpl {
    public static final String TAG = ViewModelStore.class.getSimpleName();
    private static HashMap<String, ViewModelHolder> viewModelMapping;
//    public ViewModelStore init() {
//        viewModelMapping = new HashMap<>();
//        return new ViewModelStore();
//    }

    private static ViewModelStore sViewModelStore;

    private ViewModelStore() {
    }

    public static ViewModelStore init() {
        if (sViewModelStore == null) {
            synchronized (ViewModelStore.class) {
                Log.i(TAG, " init : ViewModelStore : oh snap!,\ncheck if viewModel is recreated (This might lose stored state for activities)\nor it's just creating for first time !! ");
                viewModelMapping = new HashMap<>();
                sViewModelStore = new ViewModelStore();
                return sViewModelStore;
            }
        }
        Log.i(TAG, " init : Returning old state, good for singleton classes ");
        return sViewModelStore;
    }

//    public static void put(ViewModelHolder viewModelHolder) {
////        for (Class aClass : viewModelMapping.keySet()) {
////            if (aClass.getSimpleName().equals(viewModelHolder.getClassName().getSimpleName())) {
//        viewModelMapping.put(viewModelHolder.getClassName(), viewModelHolder);
////            }
////        }
//    }

//    public static ViewModelHolder get(Class<? extends Activity> key) {
//        for (Class<?> mappingKey : viewModelMapping.keySet()) {
//            if (mappingKey.getSimpleName().equals(key.getSimpleName())) {
//                return viewModelMapping.get(key);
//            }
//        }
//        return null;
//    }

//    public static void setState(Class<? extends Activity> key, ActivityState activityState) {
//        for (Class<?> mappingKey : viewModelMapping.keySet()) {
//            if (mappingKey.getSimpleName().equals(key.getSimpleName())) {
//                ViewModelHolder viewModelHolder = viewModelMapping.get(key);
//                viewModelHolder.setActivityState(activityState);
//            }
//        }
//    }

    @Override
    public void put(ViewModelHolder viewModelHolder) {
//        for (Class aClass : viewModelMapping.keySet()) {
//            if (aClass.getSimpleName().equals(viewModelHolder.getClassName().getSimpleName())) {
        viewModelMapping.put(viewModelHolder.getClassName(), viewModelHolder);
//            }
//        }
    }

    @Override
    public ViewModelHolder get(Class<? extends Activity> key) {
        for (String mappingKey : viewModelMapping.keySet()) {
            if (mappingKey.equals(key.getName()) || mappingKey.contains(key.getName())) {
                return viewModelMapping.get(mappingKey);
            }
        }
        return null;
    }

    @Override
    public void setState(Class<? extends Activity> key, ActivityState activityState) {
        for (String mappingKey : viewModelMapping.keySet()) {
            if (mappingKey.equals(key.getName()) || mappingKey.contains(key.getName())) {
                ViewModelHolder viewModelHolder = viewModelMapping.get(mappingKey);
                viewModelHolder.setActivityState(activityState);
            }
        }
    }
}
