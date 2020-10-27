package com.app.graffiti.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * {@link LocationUpdates} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 10/10/17
 */

public class LocationUpdates implements ResultCallback<LocationSettingsResult>, LocationListener {
    public static final String TAG = LocationUpdates.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1111;
    public static final int REQUEST_CHECK_SETTINGS = 1112;
    private static final int FASTEST_INTERVAL = 1000 * 5;
    private static final int INTERVAL = 1000 * 10;
    private GoogleApiManager manager;
    private WeakReference<Context> reference;
    private LocationRequest mLocationRequest;
    private int locationPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private boolean isLocationPrioritySatisfied = false;
    private LocationUpdateListener mListener;

    public void setLocationPriority(int locationPriority) {
        this.locationPriority = locationPriority;
        startLocationUpdates();
    }

    public void setListener(LocationUpdateListener mListener) {
        this.mListener = mListener;
    }

    public LocationUpdates(Context context, LocationUpdateListener locationUpdateListener) {
        reference = new WeakReference<Context>(context);
        manager = new GoogleApiManager(context);
        this.mListener = locationUpdateListener;
        startLocationUpdates();
    }

    public LocationUpdates(Context context, GoogleApiManager manager, LocationUpdateListener locationUpdateListener) {
        reference = new WeakReference<Context>(context);
        this.manager = manager;
        this.mListener = locationUpdateListener;
        startLocationUpdates();
    }

    public LocationUpdates(Context context, GoogleApiClient client, LocationUpdateListener locationUpdateListener) {
        reference = new WeakReference<Context>(context);
        this.manager = new GoogleApiManager(client);
        this.mListener = locationUpdateListener;
        startLocationUpdates();
    }

    public boolean isPlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        if (reference.get() != null) {
            int result = googleApiAvailability.isGooglePlayServicesAvailable(reference.get());
            if (result != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(result)) {
                    if (reference.get() instanceof Activity)
                        googleApiAvailability.getErrorDialog((Activity) reference.get(), result, PLAY_SERVICES_RESOLUTION_REQUEST);
                }
                return false;
            } else {
                return true;
            }
        } else {
            Log.i(TAG, " checkForPlayServices : Context you provided is null !");
            return false;
        }
    }

    private void startLocationUpdates() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(locationPriority);
        requestLocationAccuracy();
    }

    private void requestLocationAccuracy() {
        if (mLocationRequest == null) {
            startLocationUpdates();
        } else {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            if (manager != null && manager.getGoogleApiClient() != null) {
                if (manager.getGoogleApiClient().isConnected()) {
                    PendingResult<LocationSettingsResult> settingsRequestPendingResult =
                            LocationServices.
                                    SettingsApi.
                                    checkLocationSettings(
                                            manager.getGoogleApiClient(),
                                            builder.build()
                                    );
                    settingsRequestPendingResult.setResultCallback(this);
                } else {
                    Log.i(TAG, " requestLocationAccuracy : Client is not connected ");
                }
            } else {
                Log.i(TAG, " requestLocationAccuracy : Client is null ");
            }
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                isLocationPrioritySatisfied = true;
                getLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    if (reference.get() != null && reference.get() instanceof Activity) {
                        status.startResolutionForResult(
                                (Activity) reference.get(),
                                REQUEST_CHECK_SETTINGS);
                    } else {
                        Log.i(TAG, " onResult : Context you provided is null ! ");
                    }
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, " onResult : Location settings unavailable ! ");
                break;
        }
    }

    public void onActivityResultReceived(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                switch (resultCode) {
                    case RESULT_OK:
                        isLocationPrioritySatisfied = true;
                        getLocation();
                        break;
                    case RESULT_CANCELED:
                        isLocationPrioritySatisfied = false;
                        break;
                    default:
                        isLocationPrioritySatisfied = false;
                        break;
                }
            }
            default: {
                isLocationPrioritySatisfied = false;
                break;
            }
        }
    }

    public void getLocation() {
        if (reference.get() != null) {
            if (ActivityCompat.checkSelfPermission(reference.get(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(reference.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.wtf(TAG, " getLocation : permission denied ");
            } else {
                if (manager != null && manager.getGoogleApiClient() != null) {
                    if (manager.getGoogleApiClient().isConnected()) {
                        if (isLocationPrioritySatisfied) {
                            PendingResult<Status> result = LocationServices.
                                    FusedLocationApi.
                                    requestLocationUpdates(
                                            manager.getGoogleApiClient(),
                                            mLocationRequest,
                                            this
                                    );
                        } else {
                            onLocationChanged(
                                    LocationServices
                                            .FusedLocationApi
                                            .getLastLocation(manager.getGoogleApiClient())
                            );
                        }
                    } else {
                        Log.i(TAG, " getLocation : Client is not connected ");
                    }
                } else {
                    Log.i(TAG, " getLocation : Client is null ");
                }
            }
        } else {
            Log.i(TAG, " getLocation : Context you provided is null !");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (reference.get() != null) {
            if (location == null) {
                Log.i(TAG, " onLocationChanged : Location is null ");
                getLocation();
            } else {
                if (mListener != null) {
                    mListener.onLocationChanged(location);
                }
            }
        } else {
            removeLocationUpdates();
            Log.i(TAG, " onLocationChanged : Context you provided is null ! ");
        }
    }

    public void removeLocationUpdates() {
        if (manager != null && manager.getGoogleApiClient() != null) {
            if (manager.getGoogleApiClient().isConnected()) {
                LocationServices.
                        FusedLocationApi.
                        removeLocationUpdates(
                                manager.getGoogleApiClient(),
                                this
                        );
            } else {
                Log.i(TAG, " getLocation : Client is not connected ");
            }
        } else {
            Log.i(TAG, " getLocation : Client is null ");
        }
    }

    public interface LocationUpdateListener {
        public void onLocationChanged(Location location);
    }

    public static class GoogleApiManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private static GoogleApiClient mGoogleApiClient;

        public GoogleApiManager(GoogleApiClient client) {
            mGoogleApiClient = client;
        }

        public GoogleApiManager(Context context) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }

        public GoogleApiClient getGoogleApiClient() {
            return mGoogleApiClient;
        }

        public static void setGoogleApiClient(GoogleApiClient client) {
            mGoogleApiClient = client;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "Api client connected");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "Api client connection suspended");
            if (mGoogleApiClient != null) {
                mGoogleApiClient.reconnect();
            }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i(TAG, "Api client connection failed");
            if (mGoogleApiClient != null) {
                mGoogleApiClient.reconnect();
            }
        }
    }
}
