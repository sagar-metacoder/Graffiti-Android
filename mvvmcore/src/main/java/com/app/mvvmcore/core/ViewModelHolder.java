package com.app.mvvmcore.core;

import android.app.Activity;

import com.app.mvvmcore.lifecycle_aware.ActivityState;
import com.app.mvvmcore.lifecycle_aware.LifeCycleObserverImpl;

import java.util.HashMap;
import java.util.List;

/**
 * {@link ViewModelHolder} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 26/3/18
 */

public class ViewModelHolder implements LifeCycleObserverImpl {
    //    private Class<? extends Activity> className;
    private String className;
    private Activity activity;
    private HashMap<Class<? extends ViewModel>, ViewModel> viewModel;
    private ActivityState activityState;

//    public ViewModelHolder(Class<? extends Activity> className, Activity activity) {
//        this.className = className;
//        this.activity = activity;
////        this.viewModel = viewModel;
//    }

//    public Class<? extends Activity> getClassName() {
//        return className;
//    }

    public ViewModelHolder(String className, Activity activity) {
        this.className = className;
        this.activity = activity;
    }

    public String getClassName() {
        return className;
    }

    public Activity getActivity() {
        return activity;
    }

    public HashMap<Class<? extends ViewModel>, ViewModel> getViewModel() {
        return viewModel;
    }

    public void setViewModel(HashMap<Class<? extends ViewModel>, ViewModel> viewModel) {
        this.viewModel = viewModel;
    }

    public ActivityState getActivityState() {
        return activityState;
    }

    public void setActivityState(ActivityState activityState) {
        this.activityState = activityState;
        onStateChanged(activityState);
    }

    @Override
    public void onStateChanged(ActivityState activityState) {
        if (viewModel != null) {
            for (ViewModel viewModel : viewModel.values()) {
                if (viewModel != null) {
                    viewModel.onStateChanged(activityState);
                }
            }
        }
    }
}
