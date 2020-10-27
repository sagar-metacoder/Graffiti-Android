package com.app.graffiti;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@link MainDrawerActivity} : <p> Abstract Main drawer activity used to make common navigation drawer layout,
 * </br> across one or more activities, used to create drawer layout dynamically and it's content from child activity.
 * </br> Code that provides modularity and scalability using abstraction for dynamic layout generation.
 * </br> Checkout it's parent activity @{@link BaseActivity} for more usage.
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @see BaseActivity
 * @since 27/4/18
 */

public abstract class MainDrawerActivity extends BaseActivity {
    private static final String TAG = MainDrawerActivity.class.getSimpleName();

    // Abstract method that provide toolbar as supportActionBar.
    protected abstract Toolbar provideSupportActionbar();

    // Method to set main drawer layout.
    protected abstract View setMainDrawerContainer(@NonNull ViewGroup rootView);

    // Callback method for when drawer container is ready to use.
    protected abstract void onMainContainerReady(@Nullable Bundle savedInstanceState);

    // Method to set navigation drawer container
    protected abstract @LayoutRes
    int setNavigationDrawerContainer();

    // Main drawer layout container
    private DrawerLayout mainDrawerLayout;
    // View obj that holds navigation drawer
    private View navigationView;
    // Drawer toggle for navigation drawer
    private ActionBarDrawerToggle drawerToggle;

    public DrawerLayout getMainDrawerLayout() {
        return mainDrawerLayout;
    }

    public View getNavigationView() {
        return navigationView;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    @Override
    public ActivityConfigs setUpBaseActivity() {
        return new ActivityConfigs.Builder(-1)
                .shouldSetSupportActionBar(false)
                .create();
    }

    @Override
    public void onBaseCreated(@Nullable Bundle savedInstanceState) {
        setUpMainContainer();
        setUpMainView();
        setUpDrawerLayout();
        onMainContainerReady(savedInstanceState);
    }

    @Nullable
    @Override
    public Toolbar provideToolbar() {
        return null;
    }

    /**
     * Method to create main container as DrawerLayout & set it as root view.
     */
    private void setUpMainContainer() {
        mainDrawerLayout = new DrawerLayout(this);
        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setContentView(mainDrawerLayout);
    }

    /**
     * Method to set up main view container.
     */
    private void setUpMainView() {
        mainDrawerLayout.removeAllViews();
//        Adding main container
        ViewGroup.LayoutParams mainViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainDrawerLayout.addView(setMainDrawerContainer(mainDrawerLayout), mainViewParams);

//        Inflating navigation view
        navigationView = LayoutInflater.from(this).inflate(setNavigationDrawerContainer(), mainDrawerLayout, false);
        DrawerLayout.LayoutParams navigationViewParams = new DrawerLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        navigationViewParams.gravity = Gravity.START;
        navigationView.setLayoutParams(navigationViewParams);
        mainDrawerLayout.addView(navigationView, navigationViewParams);
    }

    /**
     * Method to set up ActionBarDrawerToggle for drawer layout and finalize.
     */
    private void setUpDrawerLayout() {
        //setSupportActionBar(provideSupportActionbar());
        drawerToggle = new ActionBarDrawerToggle(
                this,
                mainDrawerLayout,
                provideSupportActionbar(),
                R.string.app_name,
                R.string.app_name
        );
        mainDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }
}
