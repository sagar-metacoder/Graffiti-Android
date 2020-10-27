package com.app.mvvmcore.core;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * {@link ViewModelProviders} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

public class ViewModelProviders {
    public static final String TAG = ViewModelProviders.class.getSimpleName();
    private static ViewModelProviders sViewModelProviders;
    private static WeakReference<Activity> activityReference;
    private static ViewModelStore viewModelStore;

    private ViewModelProviders() {
    }

    public static ViewModelProviders of(Activity activity) {
        if (sViewModelProviders == null) {
            synchronized (ViewModelProviders.class) {
                activityReference = new WeakReference<Activity>(activity);
                viewModelStore = ViewModelStore.init();
                return new ViewModelProviders();
            }
        }
        return sViewModelProviders;
    }

    @Nullable
    public <T extends ViewModel> T get(Class<? extends ViewModel> viewModelName)
            throws InstantiationException, IllegalAccessException {
        T viewModel = (T) viewModelName.newInstance();
        HashMap<Class<? extends ViewModel>, ViewModel> viewModelMap;
        if (activityReference != null && activityReference.get() != null) {
            ViewModelHolder holder = viewModelStore.get(activityReference.get().getClass());
            if (holder != null) {
                viewModelMap = holder.getViewModel();
                if (viewModelMap != null && !viewModelMap.isEmpty()) {
                    boolean hasExistingViewModel = false;
                    for (Class<? extends ViewModel> model : viewModelMap.keySet()) {
                        if (model.equals(viewModelName)) {
                            hasExistingViewModel = true;
                        }
                    }
                    if (!hasExistingViewModel) {
                        Log.i(TAG, " get :\nViewModel does not exists, may be created for first time !\nKeep an eye on it . . . ");
                        viewModelMap.put(viewModelName, viewModel);
                    } else {
                        Log.i(TAG, " get :\nViewModel exists.. voila!,\nsurvived Config changes and lot more ");
                        viewModel = (T) viewModelMap.get(viewModelName);
                    }
                } else {
                    Log.i(TAG, " get :\nWtf, viewModel map is empty !\nAdding new one to ViewModel holder . . ");
                    viewModelMap = new HashMap<>();
                    viewModelMap.put(viewModelName, viewModel);
                }
                holder.setViewModel(viewModelMap);
            } else {
                Log.i(TAG, " get :\nNo suitable activity found for your ViewModel,\n(It happens due to stateloss) ");
            }
            return viewModel;
        } else {
            Log.i(TAG, " get :\nOh snap !,\nActivity reference state lost,\nall viewModel data is gone !!");
            return null;
        }
    }
}
