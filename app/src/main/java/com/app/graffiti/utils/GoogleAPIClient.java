package com.app.graffiti.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

/**
 * {@link GoogleAPIClient} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 6/4/18
 */

public class GoogleAPIClient
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = GoogleAPIClient.class.getSimpleName();
    private static GoogleApiClient googleApiClient;

    public static GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    private static GoogleAPIClient sApiClient;

    private GoogleAPIClient() {
    }

    public static void initApiClient(Context context) {
        if (sApiClient == null) {
            synchronized (GoogleAPIClient.class) {
                sApiClient = new GoogleAPIClient();
            }
        }
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(sApiClient)
                .addOnConnectionFailedListener(sApiClient)
                .build();
        if (googleApiClient.isConnected()) {
            googleApiClient.reconnect();
        } else {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.log(TAG, " onConnected : API client connected ", Logger.Level.INFO);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.log(TAG, " onConnectionSuspended : API client connection suspended\nState " + i, Logger.Level.INFO);
        googleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.log(TAG, " onConnectionFailed : API client connection failed\nResult " + connectionResult, Logger.Level.INFO);
        googleApiClient.unregisterConnectionCallbacks(this);
        googleApiClient.unregisterConnectionFailedListener(this);
    }
}
